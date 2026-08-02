package ir.farhangi.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementDto(
    val id: String,
    val title: String,
    val body: String,
    @SerialName("published_at") val publishedAt: String,
)
