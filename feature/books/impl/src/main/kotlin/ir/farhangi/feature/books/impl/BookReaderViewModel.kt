package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.usecase.AddPageHighlight
import ir.farhangi.core.data.usecase.ToggleBookmark
import ir.farhangi.core.data.usecase.UpdateReadingProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookReaderViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository,
    private val updateReadingProgress: UpdateReadingProgress,
    private val toggleBookmark: ToggleBookmark,
    private val addPageHighlight: AddPageHighlight,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    fun load(bookId: String) {
        observeJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, bookId = bookId) }
            when (val result = bookRepository.getBook(bookId)) {
                is Result.Success -> {
                    val book = result.data
                    val totalPages = book.totalPages.coerceAtLeast(1)
                    val userId = authRepository.observeSession().first()?.userId
                    val savedPage = if (userId != null) {
                        bookRepository.observeProgress(userId, bookId).first()?.page ?: 1
                    } else {
                        1
                    }.coerceIn(1, totalPages)

                    _uiState.value = ReaderUiState(
                        bookId = bookId,
                        bookTitle = book.title,
                        page = savedPage,
                        totalPages = totalPages,
                        isNightMode = false,
                        pageText = samplePage(book.title, savedPage),
                        isLoading = false,
                    )
                    persistProgress()
                    observePageExtras()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, pageText = result.exception.message.orEmpty())
                    }
                }
                Result.Loading -> Unit
            }
        }
    }

    fun nextPage() {
        _uiState.update { state ->
            val next = (state.page + 1).coerceAtMost(state.totalPages)
            state.copy(page = next, pageText = samplePage(state.bookTitle, next))
        }
        persistProgress()
        observePageExtras()
    }

    fun previousPage() {
        _uiState.update { state ->
            val prev = (state.page - 1).coerceAtLeast(1)
            state.copy(page = prev, pageText = samplePage(state.bookTitle, prev))
        }
        persistProgress()
        observePageExtras()
    }

    fun toggleNightMode() {
        _uiState.update { it.copy(isNightMode = !it.isNightMode) }
    }

    fun onToggleBookmark() {
        val state = _uiState.value
        viewModelScope.launch {
            toggleBookmark(state.bookId, state.page)
        }
    }

    fun onAddHighlight() {
        val state = _uiState.value
        viewModelScope.launch {
            addPageHighlight(
                bookId = state.bookId,
                page = state.page,
                text = "هایلایت صفحه ${state.page}",
            )
        }
    }

    private fun persistProgress() {
        val state = _uiState.value
        if (state.bookId.isBlank()) return
        viewModelScope.launch {
            updateReadingProgress(state.bookId, state.page, state.totalPages)
        }
    }

    private fun observePageExtras() {
        observeJob?.cancel()
        val state = _uiState.value
        if (state.bookId.isBlank()) return
        observeJob = viewModelScope.launch {
            val userId = authRepository.observeSession().first()?.userId ?: return@launch
            combine(
                bookRepository.observeBookmark(userId, state.bookId, state.page),
                bookRepository.observeHighlights(userId, state.bookId, state.page),
            ) { bookmark, highlights ->
                bookmark to highlights
            }.collect { (bookmark, highlights) ->
                _uiState.update {
                    it.copy(
                        isBookmarked = bookmark != null,
                        highlights = highlights,
                    )
                }
            }
        }
    }

    private fun samplePage(title: String, page: Int): String =
        "صفحه $page از کتاب «$title».\n\n" +
            "این متن نمونه برای نمایشگر کتاب است. پیشرفت، نشانک و هایلایت صفحه ذخیره می‌شود."
}
