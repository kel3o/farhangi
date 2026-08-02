package ir.farhangi.feature.courses.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    fun load(courseId: String) {
        this.courseId = courseId
        viewModelScope.launch {
            _uiState.value = CourseDetailUiState.Loading
            when (val result = courseRepository.getCourse(courseId)) {
                is Result.Success -> _uiState.value = CourseDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value =
                    CourseDetailUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }

    fun completeSection(sectionId: String) {
        if (courseId.isBlank()) return
        viewModelScope.launch {
            when (val result = courseRepository.completeSection(courseId, sectionId)) {
                is Result.Success -> _uiState.value = CourseDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value =
                    CourseDetailUiState.Error(result.exception.message ?: "خطا در تکمیل بخش")
                Result.Loading -> Unit
            }
        }
    }
}
