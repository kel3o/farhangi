package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.ContestReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ContestStatsUiState {
    data object Loading : ContestStatsUiState
    data class Success(val report: ContestReport) : ContestStatsUiState
    data class Error(val message: String) : ContestStatsUiState
}

@HiltViewModel
class StudioContestStatsViewModel @Inject constructor(
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContestStatsUiState>(ContestStatsUiState.Loading)
    val uiState: StateFlow<ContestStatsUiState> = _uiState.asStateFlow()

    fun load(contestId: String) {
        viewModelScope.launch {
            _uiState.value = ContestStatsUiState.Loading
            when (val result = studioRepository.getContestReport(contestId)) {
                is Result.Success -> _uiState.value = ContestStatsUiState.Success(result.data)
                is Result.Error -> _uiState.value =
                    ContestStatsUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }
}
