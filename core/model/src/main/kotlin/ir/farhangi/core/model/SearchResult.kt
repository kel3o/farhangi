package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class SearchContentType {
    BOOK,
    COURSE,
    ARTICLE,
    VIDEO,
    AUDIO,
}

@Serializable
data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val type: SearchContentType,
    val coverUrl: String? = null,
)