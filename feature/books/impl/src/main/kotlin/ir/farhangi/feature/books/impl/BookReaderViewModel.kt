package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.EngagementRepository
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
    private val engagementRepository: EngagementRepository,
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
                    val fallbackTotal = book.totalPages.coerceAtLeast(1)
                    val pages = book.pages.ifEmpty {
                        List(fallbackTotal) { index -> samplePage(book.title, index + 1) }
                    }
                    val safeTotal = pages.size.coerceAtLeast(1)
                    val userId = authRepository.observeSession().first()?.userId
                    val savedPage = if (userId != null) {
                        bookRepository.observeProgress(userId, bookId).first()?.page ?: 1
                    } else {
                        1
                    }.coerceIn(1, safeTotal)

                    _uiState.value = ReaderUiState(
                        bookId = bookId,
                        bookTitle = book.title,
                        page = savedPage,
                        totalPages = safeTotal,
                        isNightMode = false,
                        pages = pages,
                        pageText = pages[savedPage - 1],
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
        turnPage(1)
    }

    fun previousPage() {
        turnPage(-1)
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

    private fun turnPage(delta: Int) {
        val state = _uiState.value
        val next = (state.page + delta).coerceIn(1, state.totalPages)
        val text = state.pages.getOrNull(next - 1) ?: samplePage(state.bookTitle, next)
        _uiState.update { it.copy(page = next, pageText = text) }
        persistProgress()
        observePageExtras()
        viewModelScope.launch { engagementRepository.addReadingMinutes(READING_MINUTE_PER_PAGE) }
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

    companion object {
        private const val READING_MINUTE_PER_PAGE = 1
    }
}
