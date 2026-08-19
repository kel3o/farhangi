package ir.farhangi.feature.courses.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.CourseType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseCatalogViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
) : ViewModel() {
    private val type = MutableStateFlow(CourseType.PROFESSIONAL)
    private val selectedCategory = MutableStateFlow<String?>(null)
    private val coursesResult = MutableStateFlow<Result<List<Course>>?>(null)

    init {
        viewModelScope.launch { coursesResult.value = courseRepository.getCourses() }
    }

    val uiState: StateFlow<CoursesUiState> = combine(
        coursesResult.filterNotNull(),
        type,
        selectedCategory,
    ) { result, selectedType, category ->
        when (result) {
            is Result.Success -> {
                val filtered = result.data.filter { it.type == selectedType }
                CoursesUiState.Success(
                    practical = if (selectedType == CourseType.PRACTICAL) filtered else emptyList(),
                    professional = if (selectedType == CourseType.PROFESSIONAL) filtered else emptyList(),
                    selectedCategory = category,
                )
            }
            is Result.Error -> CoursesUiState.Error(result.exception.message ?: "خطا")
            Result.Loading -> CoursesUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), CoursesUiState.Loading)

    fun load(courseType: CourseType) {
        type.value = courseType
    }

    fun selectCategory(category: String?) {
        selectedCategory.value = category
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
