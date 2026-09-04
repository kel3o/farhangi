package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.CourseSection
import ir.farhangi.core.model.CourseType
import ir.farhangi.core.model.MagazineCategory
import ir.farhangi.core.model.MediaType
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.persianLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    fun createBook(title: String, author: String, body: String, durationMinutes: Int? = null) {
        viewModelScope.launch {
            val result = studioRepository.upsertBook(
                Book(
                    id = "",
                    title = title,
                    author = author.ifBlank { "ویرایشگر" },
                    description = body,
                    categories = listOf("تازه"),
                    pages = listOf(body.ifBlank { "متن تازه کتاب." }),
                    totalPages = 1,
                ),
            )
            _status.value = if (result is Result.Success) "کتاب منتشر شد" else "خطا در انتشار کتاب"
        }
    }

    fun createCourse(title: String, category: String, body: String, durationMinutes: Int? = null) {
        viewModelScope.launch {
            val result = studioRepository.upsertCourse(
                Course(
                    id = "",
                    title = title,
                    type = CourseType.PRACTICAL,
                    description = body,
                    category = category.ifBlank { "عمومی" },
                    sections = listOf(
                        CourseSection(
                            id = "s1",
                            title = title,
                            order = 1,
                            durationMinutes = 10,
                            body = body,
                        ),
                    ),
                ),
            )
            _status.value = if (result is Result.Success) "دوره منتشر شد" else "خطا در انتشار دوره"
        }
    }

    fun createArticle(title: String, category: String, body: String, durationMinutes: Int? = null) {
        viewModelScope.launch {
            val magCategory = MagazineCategory.entries.find { it.persianMatch(category) }
                ?: MagazineCategory.CULTURE
            val result = studioRepository.upsertArticle(
                Article(
                    id = "",
                    title = title,
                    type = MediaType.TEXT,
                    category = magCategory,
                    summary = body.take(SUMMARY_LIMIT),
                    body = body,
                    publishedAt = Clock.System.now(),
                ),
            )
            _status.value = if (result is Result.Success) "مطلب مجله منتشر شد" else "خطا در انتشار مطلب"
        }
    }

    fun clearStatus() {
        _status.value = null
    }

    fun createContest(
        title: String,
        category: ContestCategory,
        body: String,
        durationMinutes: Int? = null,
        sourceUrl: String? = null,
    ) {
        viewModelScope.launch {
            val durationSeconds = (durationMinutes ?: (MIN_CONTEST_MINUTES..MAX_CONTEST_MINUTES).random()) * SECONDS_PER_MINUTE
            val result = studioRepository.upsertContest(
                Contest(
                    id = "",
                    title = title,
                    summary = body,
                    category = category,
                    status = ContestStatus.LIVE,
                    questionCount = 1,
                    participantCount = 0,
                    endsAt = Instant.parse("2026-09-01T20:00:00Z"),
                    durationSeconds = durationSeconds,
                    sourceUrl = sourceUrl?.trim()?.takeIf { it.isNotEmpty() },
                ),
                listOf(
                    QuizQuestion(
                        id = "q1",
                        prompt = body.ifBlank { "سؤال نمونه" },
                        options = listOf("گزینه یک", "گزینه دو", "گزینه سه", "گزینه چهار"),
                    ),
                ),
            )
            _status.value = if (result is Result.Success) CONTEST_PUBLISHED_MESSAGE else "خطا در ثبت مسابقه"
        }
    }

    companion object {
        private const val SUMMARY_LIMIT = 80
        private const val MIN_CONTEST_MINUTES = 3
        private const val MAX_CONTEST_MINUTES = 5
        private const val SECONDS_PER_MINUTE = 60
    }
}

private fun MagazineCategory.persianMatch(raw: String): Boolean =
    persianLabel().contains(raw) || name.contains(raw, ignoreCase = true)
