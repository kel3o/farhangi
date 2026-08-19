package ir.farhangi.feature.competitions.impl

import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.QuizQuestion
import ir.farhangi.core.model.QuizSubmissionResult

sealed interface CompetitionsUiState {
    data object Loading : CompetitionsUiState
    data class Success(
        val contests: List<Contest>,
        val selectedCategory: ContestCategory?,
        val selectedStatus: ContestStatus?,
    ) : CompetitionsUiState
    data class Error(val message: String) : CompetitionsUiState
}

sealed interface ContestDetailUiState {
    data object Loading : ContestDetailUiState
    data class Success(val contest: Contest) : ContestDetailUiState
    data class Error(val message: String) : ContestDetailUiState
}

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data class Ready(
        val contestTitle: String,
        val questions: List<QuizQuestion>,
        val answers: Map<String, Int> = emptyMap(),
        val currentIndex: Int = 0,
    ) : QuizUiState
    data class Submitted(val result: QuizSubmissionResult) : QuizUiState
    data class Error(val message: String) : QuizUiState
}
