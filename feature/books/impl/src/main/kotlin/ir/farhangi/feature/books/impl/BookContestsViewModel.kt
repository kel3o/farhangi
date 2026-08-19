package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.model.ContestCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookContestsViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    val uiState: StateFlow<BookContestsUiState> = flow {
        emit(BookContestsUiState.Loading)
        when (val result = engagementRepository.getContests()) {
            is Result.Success -> emit(
                BookContestsUiState.Success(result.data.filter { it.category == ContestCategory.BOOK }),
            )
            is Result.Error -> emit(BookContestsUiState.Error(result.exception.message ?: "خطا"))
            Result.Loading -> Unit
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookContestsUiState.Loading)
}
