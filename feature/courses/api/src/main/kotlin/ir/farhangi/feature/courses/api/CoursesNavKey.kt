package ir.farhangi.feature.courses.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CoursesRoute : NavKey

@Serializable
data class CourseDetailRoute(val courseId: String) : NavKey