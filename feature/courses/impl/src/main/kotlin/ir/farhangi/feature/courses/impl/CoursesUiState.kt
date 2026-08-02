package ir.farhangi.feature.courses.impl

import ir.farhangi.core.model.Course

sealed interface CoursesUiState {
    data object Loading : CoursesUiState
    data class Success(
        val practical: List<Course>,
        val professional: List<Course>,
    ) : CoursesUiState
    data class Error(val message: String) : CoursesUiState
}

sealed interface CourseDetailUiState {
    data object Loading : CourseDetailUiState
    data class Success(val course: Course) : CourseDetailUiState
    data class Error(val message: String) : CourseDetailUiState
}