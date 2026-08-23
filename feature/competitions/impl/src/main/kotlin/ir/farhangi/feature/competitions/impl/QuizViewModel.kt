package ir.farhangi.feature.competitions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.model.DEFAULT_CONTEST_DURATION_SECONDS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private var contestId: String = ""
    private var timerJob: Job? = null
    private var submitted: Boolean = false

    fun load(id: String) {
        contestId = id
        submitted = false
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val contest = engagementRepository.getContest(id)
            val questions = engagementRepository.getQuestions(id)
            if (contest is Result.Success && questions is Result.Success) {
                val duration = contest.data.durationSeconds.takeIf { it > 0 }
                    ?: DEFAULT_CONTEST_DURATION_SECONDS
                _uiState.value = QuizUiState.Ready(
                    contestTitle = contest.data.title,
                    questions = questions.data,
                    remainingSeconds = duration,
                )
                startTimer()
            } else {
                _uiState.value = QuizUiState.Error("بارگذاری آزمون ناموفق بود")
            }
        }
    }

    fun selectOption(questionId: String, index: Int) {
        _uiState.update { state ->
            if (state is QuizUiState.Ready && !state.timedOut) {
                state.copy(answers = state.answers + (questionId to index))
            } else {
                state
            }
        }
    }

    fun next() {
        _uiState.update { state ->
            if (state is QuizUiState.Ready && !state.timedOut) {
                state.copy(currentIndex = (state.currentIndex + 1).coerceAtMost(state.questions.lastIndex))
            } else {
                state
            }
        }
    }

    fun submit() {
        finishQuiz(timedOut = false)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(TIMER_TICK_MS)
                val state = _uiState.value
                if (state !is QuizUiState.Ready) return@launch
                val next = state.remainingSeconds - 1
                if (next <= 0) {
                    _uiState.value = state.copy(remainingSeconds = 0, timedOut = true)
                    finishQuiz(timedOut = true)
                    return@launch
                }
                _uiState.value = state.copy(remainingSeconds = next)
            }
        }
    }

    private fun finishQuiz(timedOut: Boolean) {
        val state = _uiState.value
        if (state !is QuizUiState.Ready || submitted) return
        submitted = true
        timerJob?.cancel()
        viewModelScope.launch {
            when (val result = engagementRepository.submitQuiz(contestId, state.answers)) {
                is Result.Success -> _uiState.value = QuizUiState.Submitted(result.data, timedOut)
                is Result.Error -> {
                    submitted = false
                    _uiState.value = QuizUiState.Error(result.exception.message ?: "ثبت ناموفق بود")
                }
                Result.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    companion object {
        private const val TIMER_TICK_MS = 1_000L
    }
}
