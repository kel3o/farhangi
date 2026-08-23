package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class CourseType {
    PRACTICAL,
    PROFESSIONAL,
}

@Serializable
enum class LessonContentType {
    VIDEO,
    ARTICLE,
}

@Serializable
data class CourseSection(
    val id: String,
    val title: String,
    val order: Int,
    val durationMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val contentType: LessonContentType = LessonContentType.ARTICLE,
    val aparatUrl: String? = null,
    val body: String = "",
)

@Serializable
data class Course(
    val id: String,
    val title: String,
    val type: CourseType,
    val instructor: String = "",
    val coverUrl: String? = null,
    val description: String = "",
    val category: String = "",
    val isFree: Boolean = true,
    val sections: List<CourseSection> = emptyList(),
    val progress: Float = 0f,
)