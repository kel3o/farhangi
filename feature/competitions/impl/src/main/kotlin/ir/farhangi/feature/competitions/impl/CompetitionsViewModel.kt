package ir.farhangi.feature.competitions.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompetitionsViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    private val selectedCategory = MutableStateFlow<ContestCategory?>(null)
    private val selectedStatus = MutableStateFlow<ContestStatus?>(null)
    private val contestsResult = MutableStateFlow<Result<List<Contest>>?>(null)

    init {
        viewModelScope.launch { contestsResult.value = engagementRepository.getContests() }
    }

    val uiState: StateFlow<CompetitionsUiState> = combine(
        contestsResult.filterNotNull(),
        selectedCategory,
        selectedStatus,
    ) { result, category, status ->
        when (result) {
            is Result.Success -> CompetitionsUiState.Success(
                contests = result.data.filter { contest ->
                    (category == null || contest.category == category) &&
                        (status == null || contest.status == status)
                },
                selectedCategory = category,
                selectedStatus = status,
            )
            is Result.Error -> CompetitionsUiState.Error(result.exception.message ?: "خطا")
            Result.Loading -> CompetitionsUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), CompetitionsUiState.Loading)

    fun selectCategory(category: ContestCategory?) {
        selectedCategory.value = category
    }

    fun selectStatus(status: ContestStatus?) {
        selectedStatus.value = status
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
