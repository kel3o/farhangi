package ir.farhangi.feature.competitions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestDetailViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContestDetailUiState>(ContestDetailUiState.Loading)
    val uiState: StateFlow<ContestDetailUiState> = _uiState.asStateFlow()

    fun load(contestId: String) {
        viewModelScope.launch {
            _uiState.value = ContestDetailUiState.Loading
            when (val result = engagementRepository.getContest(contestId)) {
                is Result.Success -> _uiState.value = ContestDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value = ContestDetailUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }
}
