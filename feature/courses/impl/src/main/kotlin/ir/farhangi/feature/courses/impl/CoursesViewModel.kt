package ir.farhangi.feature.courses.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.model.CourseType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {
    val uiState: StateFlow<CoursesUiState> = flow {
        emit(CoursesUiState.Loading)
        when (val result = courseRepository.getCourses()) {
            is Result.Success -> emit(
                CoursesUiState.Success(
                    practical = result.data.filter { it.type == CourseType.PRACTICAL },
                    professional = result.data.filter { it.type == CourseType.PROFESSIONAL },
                ),
            )
            is Result.Error -> emit(CoursesUiState.Error(result.exception.message ?: "خطا"))
            Result.Loading -> Unit
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CoursesUiState.Loading)
}