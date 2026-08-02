package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val phone: String,
    val displayName: String = "",
    val avatarUrl: String? = null,
    val booksRead: Int = 0,
    val coursesCompleted: Int = 0,
    val readingStreakDays: Int = 0,
)