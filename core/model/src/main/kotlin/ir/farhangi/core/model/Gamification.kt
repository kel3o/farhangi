package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScoreBoard {
    OVERALL,
    READING,
    COURSES,
    CONTESTS,
    MAGAZINE,
}

fun ScoreBoard.persianLabel(): String = when (this) {
    ScoreBoard.OVERALL -> "کل امتیاز"
    ScoreBoard.READING -> "مطالعه"
    ScoreBoard.COURSES -> "آموزش"
    ScoreBoard.CONTESTS -> "مسابقه"
    ScoreBoard.MAGAZINE -> "مجله"
}

@Serializable
enum class LeaderboardPeriod {
    WEEKLY,
    MONTHLY,
}

fun LeaderboardPeriod.persianLabel(): String = when (this) {
    LeaderboardPeriod.WEEKLY -> "این هفته"
    LeaderboardPeriod.MONTHLY -> "این ماه"
}

@Serializable
data class PointsBreakdown(
    val reading: Int,
    val courses: Int,
    val contests: Int,
    val magazine: Int,
) {
    val total: Int get() = reading + courses + contests + magazine
}

@Serializable
data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val displayName: String,
    val points: Int,
    val isCurrentUser: Boolean = false,
)

@Serializable
data class Trophy(
    val id: String,
    val title: String,
    val period: LeaderboardPeriod,
    val board: ScoreBoard,
    val weekOrMonthLabel: String,
)

@Serializable
data class UserScoreSnapshot(
    val points: PointsBreakdown,
    val weeklyRank: Int?,
    val monthlyRank: Int?,
    val readingMinutesThisWeek: Int,
    val trophies: List<Trophy>,
)
