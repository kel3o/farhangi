package ir.farhangi.feature.books.impl

import ir.farhangi.core.model.Book

sealed interface BooksUiState {
    data object Loading : BooksUiState
    data class Success(val books: List<Book>) : BooksUiState
    data class Error(val message: String) : BooksUiState
}

sealed interface BookDetailUiState {
    data object Loading : BookDetailUiState
    data class Success(val book: Book) : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
}

data class ReaderUiState(
    val bookId: String = "",
    val bookTitle: String = "",
    val page: Int = 1,
    val totalPages: Int = 1,
    val isNightMode: Boolean = false,
    val pageText: String = "",
    val pages: List<String> = emptyList(),
    val isBookmarked: Boolean = false,
    val fontSizeSp: Int = 16,
    val lineHeightSp: Int = 24,
    val wordSpacingEm: Float = 0f,
    val isLoading: Boolean = true,
)
