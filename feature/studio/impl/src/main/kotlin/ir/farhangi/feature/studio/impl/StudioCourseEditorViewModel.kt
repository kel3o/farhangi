package ir.farhangi.feature.studio.impl

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.CourseCategories
import ir.farhangi.core.model.CourseSection
import ir.farhangi.core.model.CourseType
import ir.farhangi.core.model.LessonContentType
import ir.farhangi.core.model.fromPersianDigits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class StudioSectionDraft(
    val id: String = "",
    val title: String = "",
    val durationMinutes: String = "",
    val contentType: LessonContentType = LessonContentType.ARTICLE,
    val body: String = "",
    val aparatUrl: String = "",
)

data class StudioCourseDraft(
    val id: String = "",
    val title: String = "",
    val instructor: String = "",
    val category: String = CourseCategories.GENERAL,
    val type: CourseType = CourseType.PRACTICAL,
    val level: String = Course.LEVEL_BEGINNER,
    val description: String = "",
    val coverUrl: String? = null,
    val sections: List<StudioSectionDraft> = listOf(StudioSectionDraft()),
)

data class StudioCourseEditorUiState(
    val draft: StudioCourseDraft = StudioCourseDraft(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val published: Boolean = false,
)

@HiltViewModel
class StudioCourseEditorViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val studioRepository: StudioRepository,
    private val coverStore: StudioCoverStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudioCourseEditorUiState())
    val uiState: StateFlow<StudioCourseEditorUiState> = _uiState.asStateFlow()

    fun load(courseId: String) {
        if (courseId.isBlank()) {
            _uiState.value = StudioCourseEditorUiState()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = courseRepository.getCourse(courseId)) {
                is Result.Success -> {
                    val course = result.data
                    _uiState.value = StudioCourseEditorUiState(
                        draft = StudioCourseDraft(
                            id = course.id,
                            title = course.title,
                            instructor = course.instructor,
                            category = course.category.ifBlank { CourseCategories.GENERAL },
                            type = course.type,
                            level = course.level,
                            description = course.description,
                            coverUrl = course.coverUrl,
                            sections = course.sections.ifEmpty { listOf(CourseSection("", "", 1)) }
                                .map { section ->
                                    StudioSectionDraft(
                                        id = section.id,
                                        title = section.title,
                                        durationMinutes = section.durationMinutes.toString(),
                                        contentType = section.contentType,
                                        body = section.body,
                                        aparatUrl = section.aparatUrl.orEmpty(),
                                    )
                                },
                        ),
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, statusMessage = result.exception.message ?: "بارگذاری ناموفق بود")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun updateDraft(transform: (StudioCourseDraft) -> StudioCourseDraft) {
        _uiState.update { it.copy(draft = transform(it.draft), statusMessage = null) }
    }

    fun onCoverPicked(uri: Uri) {
        val persisted = coverStore.persist(uri)
        updateDraft { it.copy(coverUrl = persisted) }
    }

    fun addSection() {
        updateDraft { it.copy(sections = it.sections + StudioSectionDraft()) }
    }

    fun removeSection(index: Int) {
        updateDraft { draft ->
            if (draft.sections.size <= 1) draft
            else draft.copy(sections = draft.sections.filterIndexed { i, _ -> i != index })
        }
    }

    fun updateSection(index: Int, transform: (StudioSectionDraft) -> StudioSectionDraft) {
        updateDraft { draft ->
            draft.copy(
                sections = draft.sections.mapIndexed { i, section ->
                    if (i == index) transform(section) else section
                },
            )
        }
    }

    fun publish() {
        val draft = _uiState.value.draft
        if (draft.title.isBlank()) {
            _uiState.update { it.copy(statusMessage = "عنوان را وارد کنید") }
            return
        }
        viewModelScope.launch {
            val course = Course(
                id = draft.id,
                title = draft.title.trim(),
                type = draft.type,
                instructor = draft.instructor.trim(),
                coverUrl = draft.coverUrl,
                description = draft.description.trim(),
                category = draft.category,
                level = draft.level,
                sections = draft.sections.mapIndexed { index, section ->
                    CourseSection(
                        id = section.id.ifBlank { "s-${UUID.randomUUID()}" },
                        title = section.title.trim().ifBlank { "جلسه ${(index + 1)}" },
                        order = index + 1,
                        durationMinutes = section.durationMinutes.fromPersianDigits().toIntOrNull() ?: 0,
                        contentType = section.contentType,
                        aparatUrl = section.aparatUrl.trim().takeIf { it.isNotEmpty() },
                        body = section.body.trim(),
                    )
                },
            )
            when (studioRepository.upsertCourse(course)) {
                is Result.Success -> _uiState.update {
                    it.copy(published = true, statusMessage = COURSE_PUBLISHED_MESSAGE)
                }
                is Result.Error -> _uiState.update { it.copy(statusMessage = "خطا در انتشار دوره") }
                Result.Loading -> Unit
            }
        }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null, published = false) }
    }

    companion object {
        const val COURSE_PUBLISHED_MESSAGE = "دوره منتشر شد"
    }
}
