package com.example.data

import android.content.Context
import android.util.Log
import com.example.data.local.*
import com.example.data.remote.FirebaseRealtimeService
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TournamentRepository(
    private val database: AppDatabase,
    private val firebaseService: FirebaseRealtimeService = FirebaseRealtimeService()
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _firebaseOnline = MutableStateFlow(true)
    val firebaseOnline: StateFlow<Boolean> = _firebaseOnline.asStateFlow()

    val allTournaments: Flow<List<Tournament>> = database.tournamentDao().getAllTournaments().map { list ->
        list.map { it.toDomain() }
    }

    val allUsers: Flow<List<User>> = database.userDao().getAllUsers().map { list ->
        list.map { it.toDomain() }
    }

    val allMatches: Flow<List<Match>> = database.matchDao().getAllMatches().map { list ->
        list.map { it.toDomain() }
    }

    fun getMatchesByTournament(tournamentId: String): Flow<List<Match>> {
        return database.matchDao().getMatchesByTournament(tournamentId).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getStandingsByTournament(tournamentId: String): Flow<List<Standing>> {
        return database.standingDao().getStandingsByTournament(tournamentId).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getChatMessages(tournamentId: String): Flow<List<ChatMessage>> {
        return database.chatDao().getMessagesByTournament(tournamentId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun checkFirebaseStatus() {
        val isOnline = firebaseService.testConnection()
        _firebaseOnline.value = isOnline
    }

    suspend fun registerUser(
        email: String,
        password: String, // Kept in auth flow
        displayName: String,
        efootballId: String,
        platform: GamingPlatform,
        avatarUrl: String,
        isOrganizer: Boolean
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val existing = database.userDao().getUserByEmail(email)
            if (existing != null) {
                return@withContext Result.failure(Exception("Email already registered"))
            }

            val id = "usr_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val user = User(
                id = id,
                email = email,
                displayName = displayName,
                efootballId = efootballId,
                platform = platform,
                avatarUrl = avatarUrl,
                isOrganizer = isOrganizer,
                matchesWon = 0,
                matchesPlayed = 0,
                tournamentsWon = 0,
                bio = "eFootball Pro Player | ${platform.titleEn}"
            )

            database.userDao().insertUser(UserEntity.fromDomain(user))
            _currentUser.value = user

            // Sync with Firebase in background
            scope.launch {
                val json = JSONObject().apply {
                    put("id", user.id)
                    put("email", user.email)
                    put("displayName", user.displayName)
                    put("efootballId", user.efootballId)
                    put("platform", user.platform.name)
                    put("isOrganizer", user.isOrganizer)
                }.toString()
                firebaseService.syncUserToCloud(json, user.id)
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val entity = database.userDao().getUserByEmail(email)
            if (entity != null) {
                val user = entity.toDomain()
                _currentUser.value = user
                Result.success(user)
            } else {
                // Allow fallback demo login or create account if first time
                val fallbackUser = User(
                    id = "usr_demo",
                    email = email,
                    displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    efootballId = "782-419-503",
                    platform = GamingPlatform.MOBILE,
                    avatarUrl = "avatar_1",
                    isOrganizer = true,
                    matchesWon = 4,
                    matchesPlayed = 5,
                    tournamentsWon = 1,
                    bio = "eFootball Mobile Pro Player"
                )
                database.userDao().insertUser(UserEntity.fromDomain(fallbackUser))
                _currentUser.value = fallbackUser
                Result.success(fallbackUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        displayName: String,
        efootballId: String,
        platform: GamingPlatform,
        avatarUrl: String,
        bio: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
        val updated = current.copy(
            displayName = displayName,
            efootballId = efootballId,
            platform = platform,
            avatarUrl = avatarUrl,
            bio = bio
        )
        database.userDao().updateUser(UserEntity.fromDomain(updated))
        _currentUser.value = updated
        Result.success(updated)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun createTournament(
        title: String,
        description: String,
        imageUrl: String,
        maxPlayers: Int,
        format: TournamentFormat,
        startDate: String,
        prizes: String,
        rules: String
    ): Result<Tournament> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Must be logged in to create tournament"))
        val id = "tour_${System.currentTimeMillis()}"
        val tournament = Tournament(
            id = id,
            title = title,
            description = description,
            imageUrl = imageUrl.ifBlank { "tournament_banner" },
            maxPlayers = maxPlayers,
            format = format,
            startDate = startDate,
            prizes = prizes,
            rules = rules,
            status = TournamentStatus.REGISTRATION_OPEN,
            organizerId = user.id,
            organizerName = user.displayName,
            participantIds = listOf(user.id),
            participantCount = 1
        )

        database.tournamentDao().insertTournament(TournamentEntity.fromDomain(tournament))

        // Create initial standing row
        val initialStanding = Standing(
            playerId = user.id,
            tournamentId = id,
            playerName = user.displayName,
            playerAvatar = user.avatarUrl,
            efootballId = user.efootballId,
            platform = user.platform,
            rank = 1
        )
        database.standingDao().insertStanding(StandingEntity.fromDomain(initialStanding))

        // Sync to cloud
        scope.launch {
            val json = JSONObject().apply {
                put("id", tournament.id)
                put("title", tournament.title)
                put("maxPlayers", tournament.maxPlayers)
                put("status", tournament.status.name)
                put("organizerId", tournament.organizerId)
            }.toString()
            firebaseService.syncTournamentToCloud(json, tournament.id)
        }

        Result.success(tournament)
    }

    suspend fun joinTournament(tournamentId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Login required"))
        val entity = database.tournamentDao().getTournamentById(tournamentId) ?: return@withContext Result.failure(Exception("Tournament not found"))
        val domain = entity.toDomain()

        if (domain.participantIds.contains(user.id)) {
            return@withContext Result.failure(Exception("Already joined this tournament"))
        }

        if (domain.participantCount >= domain.maxPlayers) {
            return@withContext Result.failure(Exception("Tournament is full"))
        }

        val updatedParticipants = domain.participantIds + user.id
        val updatedTournament = domain.copy(
            participantIds = updatedParticipants,
            participantCount = updatedParticipants.size
        )

        database.tournamentDao().updateTournament(TournamentEntity.fromDomain(updatedTournament))

        // Add standing row
        val newStanding = Standing(
            playerId = user.id,
            tournamentId = tournamentId,
            playerName = user.displayName,
            playerAvatar = user.avatarUrl,
            efootballId = user.efootballId,
            platform = user.platform,
            rank = updatedParticipants.size
        )
        database.standingDao().insertStanding(StandingEntity.fromDomain(newStanding))

        Result.success(true)
    }

    suspend fun startTournamentAndGenerateMatches(tournamentId: String): Result<List<Match>> = withContext(Dispatchers.IO) {
        val entity = database.tournamentDao().getTournamentById(tournamentId) ?: return@withContext Result.failure(Exception("Tournament not found"))
        val tournament = entity.toDomain()

        // Fetch participant users
        val participants = mutableListOf<User>()
        for (pId in tournament.participantIds) {
            val u = database.userDao().getUserById(pId)?.toDomain()
            if (u != null) participants.add(u)
        }

        // If not enough real users joined, auto-populate with registered seed players to reach match bracket
        if (participants.size < 2) {
            val allOtherUsers = database.userDao().getAllUsers()
            // We ensure at least 4 or 8 players to make a realistic bracket
            val needed = (4 - participants.size).coerceAtLeast(1)
            val seedBots = listOf(
                User("bot_1", "legend@efootball.com", "NeymarJR_Fan", "482-991-034", GamingPlatform.MOBILE, "avatar_2", false, 12, 15, 2, "Mobile King"),
                User("bot_2", "cr7_ronaldo@efootball.com", "CR7_Master", "661-820-914", GamingPlatform.PLAYSTATION, "avatar_3", false, 18, 20, 3, "PS5 Division 1"),
                User("bot_3", "messi_goat@efootball.com", "MessiMagic10", "309-115-772", GamingPlatform.PC, "avatar_4", false, 9, 11, 1, "Steam eFootballer"),
                User("bot_4", "saudi_pro@efootball.com", "Al_Hilal_Eagle", "771-409-511", GamingPlatform.MOBILE, "avatar_1", false, 14, 18, 2, "eFootball Champion")
            )
            for (bot in seedBots) {
                if (participants.none { it.id == bot.id }) {
                    database.userDao().insertUser(UserEntity.fromDomain(bot))
                    participants.add(bot)
                }
            }
        }

        // Generate round 1 matches
        val generatedMatches = mutableListOf<Match>()
        var matchNumber = 1
        val shuffled = participants.shuffled()

        for (i in 0 until (shuffled.size / 2)) {
            val p1 = shuffled[i * 2]
            val p2 = shuffled[i * 2 + 1]
            val matchId = "match_${tournamentId}_${matchNumber}"
            val match = Match(
                id = matchId,
                tournamentId = tournamentId,
                matchNumber = matchNumber,
                roundName = if (shuffled.size <= 4) "نصف النهائي (Semi-Finals)" else "الجولة الأولى (Round 1)",
                roundIndex = 1,
                player1Id = p1.id,
                player1Name = p1.displayName,
                player1EfId = p1.efootballId,
                player1Avatar = p1.avatarUrl,
                player1Platform = p1.platform,
                player1Ready = false,
                player2Id = p2.id,
                player2Name = p2.displayName,
                player2EfId = p2.efootballId,
                player2Avatar = p2.avatarUrl,
                player2Platform = p2.platform,
                player2Ready = false,
                status = MatchStatus.WAITING_FOR_READY,
                scheduledTime = "اليوم 20:30 GMT"
            )
            generatedMatches.add(match)
            matchNumber++
        }

        // Save generated matches
        database.matchDao().insertMatches(generatedMatches.map { MatchEntity.fromDomain(it) })

        // Update tournament status
        val updated = tournament.copy(
            status = TournamentStatus.IN_PROGRESS,
            participantIds = participants.map { it.id },
            participantCount = participants.size
        )
        database.tournamentDao().updateTournament(TournamentEntity.fromDomain(updated))

        Result.success(generatedMatches)
    }

    suspend fun togglePlayerReady(matchId: String, playerId: String): Result<Match> = withContext(Dispatchers.IO) {
        val entity = database.matchDao().getMatchById(matchId) ?: return@withContext Result.failure(Exception("Match not found"))
        val match = entity.toDomain()

        val isP1 = match.player1Id == playerId
        val isP2 = match.player2Id == playerId

        if (!isP1 && !isP2) {
            return@withContext Result.failure(Exception("User is not a participant in this match"))
        }

        val newP1Ready = if (isP1) !match.player1Ready else match.player1Ready
        val newP2Ready = if (isP2) !match.player2Ready else match.player2Ready

        // If both become ready, switch status to PLAYERS_READY or IN_PROGRESS
        val newStatus = if (newP1Ready && newP2Ready) {
            MatchStatus.PLAYERS_READY
        } else {
            MatchStatus.WAITING_FOR_READY
        }

        val updated = match.copy(
            player1Ready = newP1Ready,
            player2Ready = newP2Ready,
            status = newStatus
        )

        database.matchDao().updateMatch(MatchEntity.fromDomain(updated))

        // Cloud sync
        scope.launch {
            val json = JSONObject().apply {
                put("matchId", updated.id)
                put("player1Ready", updated.player1Ready)
                put("player2Ready", updated.player2Ready)
                put("status", updated.status.name)
            }.toString()
            firebaseService.syncMatchToCloud(json, updated.tournamentId, updated.id)
        }

        Result.success(updated)
    }

    suspend fun submitMatchScore(
        matchId: String,
        p1Score: Int,
        p2Score: Int,
        proofImageUrl: String?,
        submittingPlayerId: String
    ): Result<Match> = withContext(Dispatchers.IO) {
        val entity = database.matchDao().getMatchById(matchId) ?: return@withContext Result.failure(Exception("Match not found"))
        val match = entity.toDomain()

        val updated = match.copy(
            player1Score = p1Score,
            player2Score = p2Score,
            proofImageUrl = proofImageUrl,
            submittedByPlayerId = submittingPlayerId,
            status = MatchStatus.RESULT_SUBMITTED
        )

        database.matchDao().updateMatch(MatchEntity.fromDomain(updated))

        scope.launch {
            val json = JSONObject().apply {
                put("matchId", updated.id)
                put("p1Score", p1Score)
                put("p2Score", p2Score)
                put("proof", proofImageUrl ?: "")
                put("status", updated.status.name)
            }.toString()
            firebaseService.syncMatchToCloud(json, updated.tournamentId, updated.id)
        }

        Result.success(updated)
    }

    suspend fun adminReviewScore(
        matchId: String,
        accept: Boolean,
        note: String? = null
    ): Result<Match> = withContext(Dispatchers.IO) {
        val entity = database.matchDao().getMatchById(matchId) ?: return@withContext Result.failure(Exception("Match not found"))
        val match = entity.toDomain()

        if (!accept) {
            val rejected = match.copy(
                status = MatchStatus.DISPUTED,
                organizerNote = note ?: "Result rejected by organizer. Please re-submit valid screenshot proof."
            )
            database.matchDao().updateMatch(MatchEntity.fromDomain(rejected))
            return@withContext Result.success(rejected)
        }

        // Accepted: determine winner
        val p1Score = match.player1Score ?: 0
        val p2Score = match.player2Score ?: 0
        val winnerId = if (p1Score > p2Score) match.player1Id else if (p2Score > p1Score) match.player2Id else null

        val approved = match.copy(
            status = MatchStatus.COMPLETED,
            winnerId = winnerId,
            organizerNote = note ?: "Result verified and approved by Tournament Organizer ✓"
        )
        database.matchDao().updateMatch(MatchEntity.fromDomain(approved))

        // Update standings
        updateTournamentStandings(match.tournamentId, match.player1Id, match.player2Id, p1Score, p2Score)

        scope.launch {
            val json = JSONObject().apply {
                put("matchId", approved.id)
                put("status", approved.status.name)
                put("winnerId", winnerId ?: "")
            }.toString()
            firebaseService.syncMatchToCloud(json, approved.tournamentId, approved.id)
        }

        Result.success(approved)
    }

    private suspend fun updateTournamentStandings(
        tournamentId: String,
        p1Id: String,
        p2Id: String,
        p1Score: Int,
        p2Score: Int
    ) {
        val currentStandings = database.standingDao().getStandingsByTournament(tournamentId)
        // We'll update the standings for both players
        val p1User = database.userDao().getUserById(p1Id)?.toDomain()
        val p2User = database.userDao().getUserById(p2Id)?.toDomain()

        val p1Won = p1Score > p2Score
        val p2Won = p2Score > p1Score
        val draw = p1Score == p2Score

        fun calc(user: User?, isWinner: Boolean, isDraw: Boolean, isLoser: Boolean, gf: Int, ga: Int): Standing {
            val p = 1
            val w = if (isWinner) 1 else 0
            val d = if (isDraw) 1 else 0
            val l = if (isLoser) 1 else 0
            val pts = if (isWinner) 3 else if (isDraw) 1 else 0
            return Standing(
                playerId = user?.id ?: "",
                tournamentId = tournamentId,
                playerName = user?.displayName ?: "Player",
                playerAvatar = user?.avatarUrl ?: "avatar_1",
                efootballId = user?.efootballId ?: "000-000-000",
                platform = user?.platform ?: GamingPlatform.MOBILE,
                played = p,
                won = w,
                drawn = d,
                lost = l,
                goalsFor = gf,
                goalsAgainst = ga,
                points = pts,
                rank = 1
            )
        }

        val s1 = calc(p1User, p1Won, draw, p2Won, p1Score, p2Score)
        val s2 = calc(p2User, p2Won, draw, p1Won, p2Score, p1Score)

        database.standingDao().insertStanding(StandingEntity.fromDomain(s1))
        database.standingDao().insertStanding(StandingEntity.fromDomain(s2))
    }

    suspend fun adminRemoveParticipant(tournamentId: String, playerId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val entity = database.tournamentDao().getTournamentById(tournamentId) ?: return@withContext Result.failure(Exception("Tournament not found"))
        val tournament = entity.toDomain()

        val updatedParticipants = tournament.participantIds.filter { it != playerId }
        val updated = tournament.copy(
            participantIds = updatedParticipants,
            participantCount = updatedParticipants.size
        )
        database.tournamentDao().updateTournament(TournamentEntity.fromDomain(updated))
        Result.success(true)
    }

    suspend fun sendChatMessage(tournamentId: String, matchId: String?, text: String): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Login required"))
        val msgId = "msg_${System.currentTimeMillis()}"
        val chat = ChatMessage(
            id = msgId,
            tournamentId = tournamentId,
            matchId = matchId,
            senderId = user.id,
            senderName = user.displayName,
            senderAvatar = user.avatarUrl,
            message = text,
            timestamp = System.currentTimeMillis(),
            isOrganizer = user.isOrganizer
        )

        database.chatDao().insertMessage(ChatMessageEntity.fromDomain(chat))

        scope.launch {
            val json = JSONObject().apply {
                put("id", chat.id)
                put("senderName", chat.senderName)
                put("message", chat.message)
                put("timestamp", chat.timestamp)
            }.toString()
            firebaseService.sendChatMessageToCloud(json, tournamentId, msgId)
        }

        Result.success(chat)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentUsers = database.userDao().getUserById("usr_admin_org")
        if (currentUsers != null) {
            // Already seeded, auto-login if needed
            if (_currentUser.value == null) {
                _currentUser.value = currentUsers.toDomain()
            }
            return@withContext
        }

        // 1. Seed Organizer and Players
        val organizer = User(
            id = "usr_admin_org",
            email = "admin@efootball-hub.com",
            displayName = "كابتن طارق (Organizer)",
            efootballId = "812-904-551",
            platform = GamingPlatform.PLAYSTATION,
            avatarUrl = "avatar_1",
            isOrganizer = true,
            matchesWon = 24,
            matchesPlayed = 28,
            tournamentsWon = 5,
            bio = "Official Tournament Host & Admin"
        )

        val player2 = User(
            id = "usr_player_2",
            email = "salah@efootball.com",
            displayName = "أحمد القناص",
            efootballId = "490-112-883",
            platform = GamingPlatform.MOBILE,
            avatarUrl = "avatar_2",
            isOrganizer = false,
            matchesWon = 18,
            matchesPlayed = 22,
            tournamentsWon = 2,
            bio = "eFootball Mobile Division 1 Rank 140"
        )

        val player3 = User(
            id = "usr_player_3",
            email = "ziad@efootball.com",
            displayName = "Ziad_Playmaker",
            efootballId = "602-841-729",
            platform = GamingPlatform.PC,
            avatarUrl = "avatar_3",
            isOrganizer = false,
            matchesWon = 14,
            matchesPlayed = 19,
            tournamentsWon = 1,
            bio = "Steam eFootball Possessional Style"
        )

        val player4 = User(
            id = "usr_player_4",
            email = "hamza@efootball.com",
            displayName = "حمزة المدافع",
            efootballId = "331-778-904",
            platform = GamingPlatform.XBOX,
            avatarUrl = "avatar_4",
            isOrganizer = false,
            matchesWon = 11,
            matchesPlayed = 16,
            tournamentsWon = 0,
            bio = "Xbox Quick Counter Specialist"
        )

        database.userDao().insertUsers(listOf(organizer, player2, player3, player4).map { UserEntity.fromDomain(it) })
        _currentUser.value = organizer

        // 2. Seed Tournaments
        val t1 = Tournament(
            id = "tour_champions_2026",
            title = "كأس أبطال eFootball الاحترافي 2026",
            description = "البطولة الكبرى للاعبي الشرق الأوسط وشمال أفريقيا على جميع المنصات مع جوائز نقدية وكوينز داخل اللعبة.",
            imageUrl = "hero_banner",
            maxPlayers = 16,
            format = TournamentFormat.KNOCKOUT,
            startDate = "2026-09-12 20:00 GMT",
            prizes = "المركز الأول: 12000 كوينز + كأس البطولة 🏆 | المركز الثاني: 6000 كوينز",
            rules = "مدة المباراة 10 دقائق، حالة اللاعبين قياسية (Regular Condition)، التمديد وركلات الترجيح مفعلة، رفع سكرين شوت النتيجة إلزامي.",
            status = TournamentStatus.IN_PROGRESS,
            organizerId = organizer.id,
            organizerName = organizer.displayName,
            participantIds = listOf(organizer.id, player2.id, player3.id, player4.id),
            participantCount = 4
        )

        val t2 = Tournament(
            id = "tour_weekend_cup",
            title = "بطولة نهاية الأسبوع الودية (Weekend Cup)",
            description = "بطولة بنظام المجموعات والتصفيات مناسبة للمبتدئين والمحترفين لزيادة تصنيف الحساب.",
            imageUrl = "ic_app_icon",
            maxPlayers = 8,
            format = TournamentFormat.GROUPS_KNOCKOUT,
            startDate = "2026-09-15 19:00 GMT",
            prizes = "3000 كوينز eFootball + بطاقة شحن",
            rules = "وقت المباراة 10 دقائق، لا تمديد في مرحلة المجموعات، الفوز 3 نقاط والتعادل نقطة.",
            status = TournamentStatus.REGISTRATION_OPEN,
            organizerId = organizer.id,
            organizerName = organizer.displayName,
            participantIds = listOf(player2.id, player3.id),
            participantCount = 2
        )

        database.tournamentDao().insertTournaments(listOf(t1, t2).map { TournamentEntity.fromDomain(it) })

        // 3. Seed Matches for Tournament 1
        val match1 = Match(
            id = "m_01_tour1",
            tournamentId = t1.id,
            matchNumber = 1,
            roundName = "نصف النهائي (Match 1)",
            roundIndex = 1,
            player1Id = organizer.id,
            player1Name = organizer.displayName,
            player1EfId = organizer.efootballId,
            player1Avatar = organizer.avatarUrl,
            player1Platform = organizer.platform,
            player1Ready = true,
            player2Id = player2.id,
            player2Name = player2.displayName,
            player2EfId = player2.efootballId,
            player2Avatar = player2.avatarUrl,
            player2Platform = player2.platform,
            player2Ready = true,
            player1Score = 3,
            player2Score = 1,
            status = MatchStatus.RESULT_SUBMITTED,
            proofImageUrl = "proof_sample",
            submittedByPlayerId = organizer.id,
            scheduledTime = "اليوم 20:00 GMT",
            organizerNote = "النتيجة قيد المراجعة: 3 - 1 لكابتن طارق"
        )

        val match2 = Match(
            id = "m_02_tour1",
            tournamentId = t1.id,
            matchNumber = 2,
            roundName = "نصف النهائي (Match 2)",
            roundIndex = 1,
            player1Id = player3.id,
            player1Name = player3.displayName,
            player1EfId = player3.efootballId,
            player1Avatar = player3.avatarUrl,
            player1Platform = player3.platform,
            player1Ready = true,
            player2Id = player4.id,
            player2Name = player4.displayName,
            player2EfId = player4.efootballId,
            player2Avatar = player4.avatarUrl,
            player2Platform = player4.platform,
            player2Ready = false,
            status = MatchStatus.WAITING_FOR_READY,
            scheduledTime = "اليوم 20:45 GMT"
        )

        database.matchDao().insertMatches(listOf(match1, match2).map { MatchEntity.fromDomain(it) })

        // 4. Seed Standings
        val s1 = Standing(organizer.id, t1.id, organizer.displayName, organizer.avatarUrl, organizer.efootballId, organizer.platform, 1, 1, 0, 0, 3, 1, 3, 1)
        val s2 = Standing(player3.id, t1.id, player3.displayName, player3.avatarUrl, player3.efootballId, player3.platform, 0, 0, 0, 0, 0, 0, 0, 2)
        val s3 = Standing(player4.id, t1.id, player4.displayName, player4.avatarUrl, player4.efootballId, player4.platform, 0, 0, 0, 0, 0, 0, 0, 3)
        val s4 = Standing(player2.id, t1.id, player2.displayName, player2.avatarUrl, player2.efootballId, player2.platform, 1, 0, 0, 1, 1, 3, 0, 4)

        database.standingDao().insertStandings(listOf(s1, s2, s3, s4).map { StandingEntity.fromDomain(it) })

        // 5. Seed Chat Messages
        val c1 = ChatMessage("c_1", t1.id, null, organizer.id, organizer.displayName, organizer.avatarUrl, "مرحبًا بكم في كأس أبطال eFootball 2026! يرجى من جميع اللاعبين التأكد من حالة الجاهزية والاتصال بالإنترنت.", System.currentTimeMillis() - 600000, true)
        val c2 = ChatMessage("c_2", t1.id, null, player2.id, player2.displayName, player2.avatarUrl, "بالتوفيق للجميع! لقد أضفت معرّف الكابتن وسنبدأ الآن داخل eFootball.", System.currentTimeMillis() - 300000, false)
        val c3 = ChatMessage("c_3", t1.id, null, player3.id, player3.displayName, player3.avatarUrl, "مستعد لمباراة نصف النهائي الثانية ⚡", System.currentTimeMillis() - 120000, false)

        database.chatDao().insertMessages(listOf(c1, c2, c3).map { ChatMessageEntity.fromDomain(it) })
    }
}
