package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.BookCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {
    private val selectedCategory = MutableStateFlow<String?>(null)
    private val booksResult = MutableStateFlow<Result<List<Book>>?>(null)

    init {
        viewModelScope.launch { booksResult.value = bookRepository.getBooks() }
    }

    val uiState: StateFlow<LibraryUiState> = combine(
        booksResult.filterNotNull(),
        selectedCategory,
    ) { result, category ->
        when (result) {
            is Result.Success -> {
                val all = result.data
                val categories = BookCategories.ALL
                LibraryUiState.Success(
                    books = if (category == null) all else all.filter { category in it.categories },
                    categories = categories,
                    selectedCategory = category,
                )
            }
            is Result.Error -> LibraryUiState.Error(result.exception.message ?: "خطا")
            Result.Loading -> LibraryUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), LibraryUiState.Loading)

    fun selectCategory(category: String?) {
        selectedCategory.value = category
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {
    val uiState: StateFlow<LibraryUiState> = kotlinx.coroutines.flow.flow {
        emit(LibraryUiState.Loading)
        when (val result = bookRepository.getBooks()) {
            is Result.Success -> emit(
                LibraryUiState.Success(
                    books = result.data.filter { it.isSaved },
                    categories = emptyList(),
                    selectedCategory = null,
                ),
            )
            is Result.Error -> emit(LibraryUiState.Error(result.exception.message ?: "خطا"))
            Result.Loading -> Unit
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState.Loading)
}
