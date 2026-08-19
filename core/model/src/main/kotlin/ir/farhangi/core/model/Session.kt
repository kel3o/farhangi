package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val userId: String,
    val phone: String,
    val accessToken: String,
    val refreshToken: String? = null,
    val displayName: String? = null,
    val role: UserRole = UserRole.USER,
)