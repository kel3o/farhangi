package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.BookRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {
    val uiState: StateFlow<BooksUiState> = flow {
        emit(BooksUiState.Loading)
        when (val result = bookRepository.getBooks()) {
            is Result.Success -> emit(BooksUiState.Success(result.data))
            is Result.Error -> emit(BooksUiState.Error(result.exception.message ?: "خطا"))
            Result.Loading -> Unit
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BooksUiState.Loading)
}