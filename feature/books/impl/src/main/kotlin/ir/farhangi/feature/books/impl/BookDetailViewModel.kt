package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.EngagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val engagementRepository: EngagementRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun load(bookId: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            when (val result = bookRepository.getBook(bookId)) {
                is Result.Success -> _uiState.value = BookDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value =
                    BookDetailUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }

    fun toggleSaved() {
        val current = _uiState.value
        if (current !is BookDetailUiState.Success) return
        viewModelScope.launch {
            engagementRepository.toggleSavedBook(current.book.id)
            load(current.book.id)
        }
    }
}