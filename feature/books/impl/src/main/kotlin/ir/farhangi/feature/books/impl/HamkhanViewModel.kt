package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.ScoreBoard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HamkhanViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    private val period = MutableStateFlow(LeaderboardPeriod.WEEKLY)
    private val board = MutableStateFlow(ScoreBoard.OVERALL)

    val uiState: StateFlow<HamkhanUiState> = combine(period, board) { selectedPeriod, selectedBoard ->
        selectedPeriod to selectedBoard
    }.flatMapLatest { (selectedPeriod, selectedBoard) ->
        flow {
            emit(HamkhanUiState.Loading)
            when (val result = engagementRepository.getLeaderboard(selectedPeriod, selectedBoard)) {
                is Result.Success -> emit(HamkhanUiState.Success(selectedPeriod, selectedBoard, result.data))
                is Result.Error -> emit(HamkhanUiState.Error(result.exception.message ?: "خطا"))
                Result.Loading -> Unit
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HamkhanUiState.Loading)

    fun selectPeriod(value: LeaderboardPeriod) {
        period.value = value
    }

    fun selectBoard(value: ScoreBoard) {
        board.value = value
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
