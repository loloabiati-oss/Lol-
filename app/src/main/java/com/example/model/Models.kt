package com.example.model

enum class GamingPlatform(val titleEn: String, val titleAr: String, val iconName: String) {
    MOBILE("Mobile (Android/iOS)", "هاتف (Android/iOS)", "phone_android"),
    PLAYSTATION("PlayStation 5", "بلايستيشن 5", "sports_esports"),
    XBOX("Xbox Series X/S", "إكس بوكس", "gamepad"),
    PC("PC / Steam", "كمبيوتر / ستيم", "computer");

    companion object {
        fun fromString(value: String): GamingPlatform {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.titleEn.equals(value, ignoreCase = true) } ?: MOBILE
        }
    }
}

enum class TournamentFormat(val titleAr: String, val titleEn: String) {
    KNOCKOUT("خروج المغلوب", "Single Elimination"),
    GROUPS_KNOCKOUT("مجموعات + تصفيات", "Groups + Knockout"),
    ROUND_ROBIN("دوري النقاط", "Round Robin League");

    companion object {
        fun fromString(value: String): TournamentFormat {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: KNOCKOUT
        }
    }
}

enum class TournamentStatus(val titleAr: String, val titleEn: String) {
    REGISTRATION_OPEN("التسجيل مفتوح", "Registration Open"),
    STARTED("بدأت", "Started"),
    IN_PROGRESS("جارية الآن", "In Progress"),
    COMPLETED("انتهت", "Completed");

    companion object {
        fun fromString(value: String): TournamentStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: REGISTRATION_OPEN
        }
    }
}

enum class MatchStatus(val titleAr: String, val titleEn: String) {
    SCHEDULED("مجدولة", "Scheduled"),
    WAITING_FOR_READY("في انتظار الجاهزية", "Waiting for Ready"),
    PLAYERS_READY("اللاعبان جاهزان - ابدأ في eFootball", "Both Ready - Start in Game"),
    IN_PROGRESS("المباراة جارية", "Match In Progress"),
    RESULT_SUBMITTED("بانتظار مراجعة النتيجة", "Result Pending Review"),
    COMPLETED("معتمدة وانتهت", "Result Approved"),
    DISPUTED("اعتراض / مرفوضة", "Disputed / Rejected");

    companion object {
        fun fromString(value: String): MatchStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: SCHEDULED
        }
    }
}

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val efootballId: String,
    val platform: GamingPlatform,
    val avatarUrl: String,
    val isOrganizer: Boolean = false,
    val matchesWon: Int = 0,
    val matchesPlayed: Int = 0,
    val tournamentsWon: Int = 0,
    val bio: String = ""
)

data class Tournament(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val maxPlayers: Int = 16,
    val format: TournamentFormat = TournamentFormat.KNOCKOUT,
    val startDate: String,
    val prizes: String,
    val rules: String,
    val status: TournamentStatus = TournamentStatus.REGISTRATION_OPEN,
    val organizerId: String,
    val organizerName: String,
    val participantIds: List<String> = emptyList(),
    val participantCount: Int = 0
)

data class Match(
    val id: String,
    val tournamentId: String,
    val matchNumber: Int,
    val roundName: String,
    val roundIndex: Int = 1,
    val player1Id: String,
    val player1Name: String,
    val player1EfId: String,
    val player1Avatar: String,
    val player1Platform: GamingPlatform,
    val player1Ready: Boolean = false,
    val player2Id: String,
    val player2Name: String,
    val player2EfId: String,
    val player2Avatar: String,
    val player2Platform: GamingPlatform,
    val player2Ready: Boolean = false,
    val player1Score: Int? = null,
    val player2Score: Int? = null,
    val status: MatchStatus = MatchStatus.WAITING_FOR_READY,
    val proofImageUrl: String? = null,
    val submittedByPlayerId: String? = null,
    val winnerId: String? = null,
    val scheduledTime: String = "20:00 GMT",
    val organizerNote: String? = null
)

data class Standing(
    val playerId: String,
    val tournamentId: String,
    val playerName: String,
    val playerAvatar: String,
    val efootballId: String,
    val platform: GamingPlatform,
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val points: Int = 0,
    val rank: Int = 1
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}

data class ChatMessage(
    val id: String,
    val tournamentId: String,
    val matchId: String? = null,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isOrganizer: Boolean = false
)
