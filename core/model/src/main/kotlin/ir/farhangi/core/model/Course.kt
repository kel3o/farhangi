package ir.farhangi.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class CourseType {
    PRACTICAL,
    PROFESSIONAL,
}

@Serializable
data class CourseSection(
    val id: String,
    val title: String,
    val order: Int,
    val durationMinutes: Int = 0,
    val isCompleted: Boolean = false,
)

@Serializable
data class Course(
    val id: String,
    val title: String,
    val type: CourseType,
    val coverUrl: String? = null,
    val description: String = "",
    val sections: List<CourseSection> = emptyList(),
    val progress: Float = 0f,
)