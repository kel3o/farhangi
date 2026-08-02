package ir.farhangi.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    @SerialName("cover_url") val coverUrl: String? = null,
    val categories: List<String> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 0,
    val rating: Double? = null,
    val description: String = "",
)

@Serializable
data class CourseSectionDto(
    val id: String,
    val title: String,
    val order: Int,
    @SerialName("duration_minutes") val durationMinutes: Int = 0,
    @SerialName("is_completed") val isCompleted: Boolean = false,
)

@Serializable
data class CourseDto(
    val id: String,
    val title: String,
    val type: String,
    @SerialName("cover_url") val coverUrl: String? = null,
    val description: String = "",
    val sections: List<CourseSectionDto> = emptyList(),
    val progress: Float = 0f,
)

@Serializable
data class ArticleDto(
    val id: String,
    val title: String,
    val type: String,
    val category: String,
    val summary: String = "",
    val body: String = "",
    @SerialName("media_url") val mediaUrl: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("published_at") val publishedAt: String,
)

@Serializable
data class SearchResultDto(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val type: String,
    @SerialName("cover_url") val coverUrl: String? = null,
)