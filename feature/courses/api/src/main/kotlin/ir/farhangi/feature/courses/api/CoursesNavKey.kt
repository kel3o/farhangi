package ir.farhangi.feature.courses.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CoursesRoute : NavKey

@Serializable
data object ProfessionalCatalogRoute : NavKey

@Serializable
data object PracticalCatalogRoute : NavKey

@Serializable
data class CourseDetailRoute(val courseId: String) : NavKey

@Serializable
data class LessonRoute(val courseId: String, val sectionId: String) : NavKey
