package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val efootballId: String,
    val platform: String,
    val avatarUrl: String,
    val isOrganizer: Boolean,
    val matchesWon: Int,
    val matchesPlayed: Int,
    val tournamentsWon: Int,
    val bio: String
) {
    fun toDomain() = User(
        id = id,
        email = email,
        displayName = displayName,
        efootballId = efootballId,
        platform = GamingPlatform.fromString(platform),
        avatarUrl = avatarUrl,
        isOrganizer = isOrganizer,
        matchesWon = matchesWon,
        matchesPlayed = matchesPlayed,
        tournamentsWon = tournamentsWon,
        bio = bio
    )

    companion object {
        fun fromDomain(u: User) = UserEntity(
            id = u.id,
            email = u.email,
            displayName = u.displayName,
            efootballId = u.efootballId,
            platform = u.platform.name,
            avatarUrl = u.avatarUrl,
            isOrganizer = u.isOrganizer,
            matchesWon = u.matchesWon,
            matchesPlayed = u.matchesPlayed,
            tournamentsWon = u.tournamentsWon,
            bio = u.bio
        )
    }
}

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val maxPlayers: Int,
    val format: String,
    val startDate: String,
    val prizes: String,
    val rules: String,
    val status: String,
    val organizerId: String,
    val organizerName: String,
    val participantIdsCsv: String,
    val participantCount: Int
) {
    fun toDomain() = Tournament(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        maxPlayers = maxPlayers,
        format = TournamentFormat.fromString(format),
        startDate = startDate,
        prizes = prizes,
        rules = rules,
        status = TournamentStatus.fromString(status),
        organizerId = organizerId,
        organizerName = organizerName,
        participantIds = if (participantIdsCsv.isBlank()) emptyList() else participantIdsCsv.split(","),
        participantCount = participantCount
    )

    companion object {
        fun fromDomain(t: Tournament) = TournamentEntity(
            id = t.id,
            title = t.title,
            description = t.description,
            imageUrl = t.imageUrl,
            maxPlayers = t.maxPlayers,
            format = t.format.name,
            startDate = t.startDate,
            prizes = t.prizes,
            rules = t.rules,
            status = t.status.name,
            organizerId = t.organizerId,
            organizerName = t.organizerName,
            participantIdsCsv = t.participantIds.joinToString(","),
            participantCount = t.participantCount
        )
    }
}

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val matchNumber: Int,
    val roundName: String,
    val roundIndex: Int,
    val player1Id: String,
    val player1Name: String,
    val player1EfId: String,
    val player1Avatar: String,
    val player1Platform: String,
    val player1Ready: Boolean,
    val player2Id: String,
    val player2Name: String,
    val player2EfId: String,
    val player2Avatar: String,
    val player2Platform: String,
    val player2Ready: Boolean,
    val player1Score: Int?,
    val player2Score: Int?,
    val status: String,
    val proofImageUrl: String?,
    val submittedByPlayerId: String?,
    val winnerId: String?,
    val scheduledTime: String,
    val organizerNote: String?
) {
    fun toDomain() = Match(
        id = id,
        tournamentId = tournamentId,
        matchNumber = matchNumber,
        roundName = roundName,
        roundIndex = roundIndex,
        player1Id = player1Id,
        player1Name = player1Name,
        player1EfId = player1EfId,
        player1Avatar = player1Avatar,
        player1Platform = GamingPlatform.fromString(player1Platform),
        player1Ready = player1Ready,
        player2Id = player2Id,
        player2Name = player2Name,
        player2EfId = player2EfId,
        player2Avatar = player2Avatar,
        player2Platform = GamingPlatform.fromString(player2Platform),
        player2Ready = player2Ready,
        player1Score = player1Score,
        player2Score = player2Score,
        status = MatchStatus.fromString(status),
        proofImageUrl = proofImageUrl,
        submittedByPlayerId = submittedByPlayerId,
        winnerId = winnerId,
        scheduledTime = scheduledTime,
        organizerNote = organizerNote
    )

    companion object {
        fun fromDomain(m: Match) = MatchEntity(
            id = m.id,
            tournamentId = m.tournamentId,
            matchNumber = m.matchNumber,
            roundName = m.roundName,
            roundIndex = m.roundIndex,
            player1Id = m.player1Id,
            player1Name = m.player1Name,
            player1EfId = m.player1EfId,
            player1Avatar = m.player1Avatar,
            player1Platform = m.player1Platform.name,
            player1Ready = m.player1Ready,
            player2Id = m.player2Id,
            player2Name = m.player2Name,
            player2EfId = m.player2EfId,
            player2Avatar = m.player2Avatar,
            player2Platform = m.player2Platform.name,
            player2Ready = m.player2Ready,
            player1Score = m.player1Score,
            player2Score = m.player2Score,
            status = m.status.name,
            proofImageUrl = m.proofImageUrl,
            submittedByPlayerId = m.submittedByPlayerId,
            winnerId = m.winnerId,
            scheduledTime = m.scheduledTime,
            organizerNote = m.organizerNote
        )
    }
}

@Entity(tableName = "standings", primaryKeys = ["playerId", "tournamentId"])
data class StandingEntity(
    val playerId: String,
    val tournamentId: String,
    val playerName: String,
    val playerAvatar: String,
    val efootballId: String,
    val platform: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
    val rank: Int
) {
    fun toDomain() = Standing(
        playerId = playerId,
        tournamentId = tournamentId,
        playerName = playerName,
        playerAvatar = playerAvatar,
        efootballId = efootballId,
        platform = GamingPlatform.fromString(platform),
        played = played,
        won = won,
        drawn = drawn,
        lost = lost,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        points = points,
        rank = rank
    )

    companion object {
        fun fromDomain(s: Standing) = StandingEntity(
            playerId = s.playerId,
            tournamentId = s.tournamentId,
            playerName = s.playerName,
            playerAvatar = s.playerAvatar,
            efootballId = s.efootballId,
            platform = s.platform.name,
            played = s.played,
            won = s.won,
            drawn = s.drawn,
            lost = s.lost,
            goalsFor = s.goalsFor,
            goalsAgainst = s.goalsAgainst,
            points = s.points,
            rank = s.rank
        )
    }
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val matchId: String?,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val message: String,
    val timestamp: Long,
    val isOrganizer: Boolean
) {
    fun toDomain() = ChatMessage(
        id = id,
        tournamentId = tournamentId,
        matchId = matchId,
        senderId = senderId,
        senderName = senderName,
        senderAvatar = senderAvatar,
        message = message,
        timestamp = timestamp,
        isOrganizer = isOrganizer
    )

    companion object {
        fun fromDomain(c: ChatMessage) = ChatMessageEntity(
            id = c.id,
            tournamentId = c.tournamentId,
            matchId = c.matchId,
            senderId = c.senderId,
            senderName = c.senderName,
            senderAvatar = c.senderAvatar,
            message = c.message,
            timestamp = c.timestamp,
            isOrganizer = c.isOrganizer
        )
    }
}
