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
class LessonViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()
    private var courseId: String = ""
    private var sectionId: String = ""

    fun load(courseId: String, sectionId: String) {
        this.courseId = courseId
        this.sectionId = sectionId
        viewModelScope.launch {
            _uiState.value = LessonUiState.Loading
            when (val result = courseRepository.getCourse(courseId)) {
                is Result.Success -> {
                    val section = result.data.sections.find { it.id == sectionId }
                    _uiState.value = if (section != null) {
                        LessonUiState.Success(section)
                    } else {
                        LessonUiState.Error("جلسه یافت نشد")
                    }
                }
                is Result.Error -> _uiState.value = LessonUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }

    fun complete() {
        viewModelScope.launch {
            courseRepository.completeSection(courseId, sectionId)
            load(courseId, sectionId)
        }
    }
}
