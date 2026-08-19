package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val phone: String,
    val displayName: String = "",
    val avatarUrl: String? = null,
    val role: UserRole = UserRole.USER,
    val booksRead: Int = 0,
    val coursesCompleted: Int = 0,
    val readingStreakDays: Int = 0,
    val points: PointsBreakdown = PointsBreakdown(0, 0, 0, 0),
    val trophies: List<Trophy> = emptyList(),
)