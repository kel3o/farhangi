package ir.farhangi.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    TEXT,
    VIDEO,
    AUDIO,
    PODCAST,
    SPEECH,
    NEWS,
}

@Serializable
data class Article(
    val id: String,
    val title: String,
    val type: MediaType,
    val category: MagazineCategory,
    val summary: String = "",
    val body: String = "",
    val mediaUrl: String? = null,
    val coverUrl: String? = null,
    val publishedAt: Instant,
    val isSaved: Boolean = false,
)