package ir.farhangi.feature.competitions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private var contestId: String = ""

    fun load(id: String) {
        contestId = id
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val contest = engagementRepository.getContest(id)
            val questions = engagementRepository.getQuestions(id)
            if (contest is Result.Success && questions is Result.Success) {
                _uiState.value = QuizUiState.Ready(
                    contestTitle = contest.data.title,
                    questions = questions.data,
                )
            } else {
                _uiState.value = QuizUiState.Error("بارگذاری آزمون ناموفق بود")
            }
        }
    }

    fun selectOption(questionId: String, index: Int) {
        _uiState.update { state ->
            if (state is QuizUiState.Ready) {
                state.copy(answers = state.answers + (questionId to index))
            } else {
                state
            }
        }
    }

    fun next() {
        _uiState.update { state ->
            if (state is QuizUiState.Ready) {
                state.copy(currentIndex = (state.currentIndex + 1).coerceAtMost(state.questions.lastIndex))
            } else {
                state
            }
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state !is QuizUiState.Ready) return
        viewModelScope.launch {
            when (val result = engagementRepository.submitQuiz(contestId, state.answers)) {
                is Result.Success -> _uiState.value = QuizUiState.Submitted(result.data)
                is Result.Error -> _uiState.value = QuizUiState.Error(result.exception.message ?: "ثبت ناموفق بود")
                Result.Loading -> Unit
            }
        }
    }
}
