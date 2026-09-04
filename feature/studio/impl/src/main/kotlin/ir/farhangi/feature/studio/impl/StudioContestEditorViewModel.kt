package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.QUIZ_OPTION_COUNT
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.fromPersianDigits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

data class StudioQuestionDraft(
    val id: String = "",
    val prompt: String = "",
    val options: List<String> = List(QUIZ_OPTION_COUNT) { "" },
    val correctIndex: Int = 0,
)

data class StudioContestDraft(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val category: ContestCategory = ContestCategory.BOOK,
    val status: ContestStatus = ContestStatus.LIVE,
    val durationMinutes: String = "3",
    val endsInDays: String = "7",
    val hasSource: Boolean = false,
    val sourceUrl: String = "",
    val existingParticipantCount: Int = 0,
    val questions: List<StudioQuestionDraft> = listOf(StudioQuestionDraft()),
)

data class StudioContestEditorUiState(
    val draft: StudioContestDraft = StudioContestDraft(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val published: Boolean = false,
)

@HiltViewModel
class StudioContestEditorViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudioContestEditorUiState())
    val uiState: StateFlow<StudioContestEditorUiState> = _uiState.asStateFlow()

    fun load(contestId: String) {
        if (contestId.isBlank()) {
            _uiState.value = StudioContestEditorUiState()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val contestResult = engagementRepository.getContest(contestId)
            val questionsResult = engagementRepository.getQuestions(contestId)
            if (contestResult is Result.Error) {
                _uiState.update {
                    it.copy(isLoading = false, statusMessage = contestResult.exception.message ?: "بارگذاری ناموفق بود")
                }
                return@launch
            }
            val contest = (contestResult as Result.Success).data
            val questions = (questionsResult as? Result.Success)?.data.orEmpty()
            val remainingDays = daysUntil(contest.endsAt).coerceAtLeast(0)
            _uiState.value = StudioContestEditorUiState(
                draft = StudioContestDraft(
                    id = contest.id,
                    title = contest.title,
                    summary = contest.summary,
                    category = contest.category,
                    status = contest.status,
                    durationMinutes = (contest.durationSeconds / SECONDS_PER_MINUTE).toString(),
                    endsInDays = remainingDays.toString(),
                    hasSource = contest.hasSource,
                    sourceUrl = contest.sourceUrl.orEmpty(),
                    existingParticipantCount = contest.participantCount,
                    questions = questions.ifEmpty { listOf(QuizQuestion("", "", List(QUIZ_OPTION_COUNT) { "" })) }
                        .map { question ->
                            StudioQuestionDraft(
                                id = question.id,
                                prompt = question.prompt,
                                options = question.options.padOptions(),
                                correctIndex = question.correctIndex.coerceIn(0, QUIZ_OPTION_COUNT - 1),
                            )
                        },
                ),
            )
        }
    }

    fun updateDraft(transform: (StudioContestDraft) -> StudioContestDraft) {
        _uiState.update { it.copy(draft = transform(it.draft), statusMessage = null) }
    }

    fun addQuestion() {
        updateDraft { it.copy(questions = it.questions + StudioQuestionDraft()) }
    }

    fun removeQuestion(index: Int) {
        updateDraft { draft ->
            if (draft.questions.size <= 1) draft
            else draft.copy(questions = draft.questions.filterIndexed { i, _ -> i != index })
        }
    }

    fun updateQuestion(index: Int, transform: (StudioQuestionDraft) -> StudioQuestionDraft) {
        updateDraft { draft ->
            draft.copy(
                questions = draft.questions.mapIndexed { i, question ->
                    if (i == index) transform(question) else question
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
        if (draft.questions.any { it.prompt.isBlank() || it.options.any { option -> option.isBlank() } }) {
            _uiState.update { it.copy(statusMessage = "سؤال‌ها و گزینه‌ها را کامل کنید") }
            return
        }
        viewModelScope.launch {
            val durationMinutes = draft.durationMinutes.fromPersianDigits().toIntOrNull() ?: DEFAULT_DURATION_MINUTES
            val days = draft.endsInDays.fromPersianDigits().toIntOrNull() ?: DEFAULT_END_DAYS
            val contest = Contest(
                id = draft.id,
                title = draft.title.trim(),
                summary = draft.summary.trim(),
                category = draft.category,
                status = draft.status,
                questionCount = draft.questions.size,
                participantCount = draft.existingParticipantCount,
                endsAt = Clock.System.now() + days.days,
                durationSeconds = durationMinutes * SECONDS_PER_MINUTE,
                sourceUrl = draft.sourceUrl.trim().takeIf { draft.hasSource && it.isNotEmpty() },
            )
            val questions = draft.questions.map { question ->
                QuizQuestion(
                    id = question.id.ifBlank { "q-${UUID.randomUUID()}" },
                    prompt = question.prompt.trim(),
                    options = question.options,
                    correctIndex = question.correctIndex.coerceIn(0, question.options.lastIndex),
                )
            }
            when (studioRepository.upsertContest(contest, questions)) {
                is Result.Success -> _uiState.update {
                    it.copy(published = true, statusMessage = CONTEST_PUBLISHED_MESSAGE)
                }
                is Result.Error -> _uiState.update { it.copy(statusMessage = "خطا در ثبت مسابقه") }
                Result.Loading -> Unit
            }
        }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null, published = false) }
    }

    private fun daysUntil(endsAt: Instant): Int {
        val remainingMs = (endsAt.toEpochMilliseconds() - Clock.System.now().toEpochMilliseconds())
            .coerceAtLeast(0L)
        return (remainingMs / MILLIS_PER_DAY).toInt()
    }

    private fun List<String>.padOptions(): List<String> {
        val padded = toMutableList()
        while (padded.size < QUIZ_OPTION_COUNT) padded += ""
        return padded.take(QUIZ_OPTION_COUNT)
    }

    companion object {
        const val CONTEST_PUBLISHED_MESSAGE = "مسابقه ثبت شد"
        private const val SECONDS_PER_MINUTE = 60
        private const val DEFAULT_DURATION_MINUTES = 3
        private const val DEFAULT_END_DAYS = 7
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}
