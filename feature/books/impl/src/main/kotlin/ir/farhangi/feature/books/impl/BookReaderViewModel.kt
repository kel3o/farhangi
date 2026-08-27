package ir.farhangi.feature.books.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.data.usecase.ToggleBookmark
import ir.farhangi.core.data.usecase.UpdateReadingProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                        fontSizeSp = FONT_DEFAULT_SP,
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

    fun jumpToPage(page: Int) {
        val state = _uiState.value
        val target = page.coerceIn(1, state.totalPages)
        val text = state.pages.getOrNull(target - 1) ?: samplePage(state.bookTitle, target)
        _uiState.update { it.copy(page = target, pageText = text) }
        persistProgress()
        observePageExtras()
    }

    fun toggleNightMode() {
        _uiState.update { it.copy(isNightMode = !it.isNightMode) }
    }

    fun setFontSizeSp(sizeSp: Int) {
        _uiState.update {
            it.copy(fontSizeSp = sizeSp.coerceIn(FONT_MIN_SP, FONT_MAX_SP))
        }
    }

    fun onToggleBookmark() {
        val state = _uiState.value
        viewModelScope.launch {
            toggleBookmark(state.bookId, state.page, state.bookTitle)
        }
    }

    private fun turnPage(delta: Int) {
        val state = _uiState.value
        val next = (state.page + delta).coerceIn(1, state.totalPages)
        if (next == state.page) return
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
            bookRepository.observeBookmark(userId, state.bookId, state.page).collect { bookmark ->
                _uiState.update { it.copy(isBookmarked = bookmark != null) }
            }
        }
    }

    private fun samplePage(title: String, page: Int): String =
        "صفحه $page از کتاب «$title».\n\n" +
            "در این صفحه می‌خوانید که چگونه اندیشه‌های کوتاه، مسیر زندگی روزمره را روشن می‌کنند.\n" +
            "هر بند فرصتی است برای مکث؛ نه شتاب برای تمام کردن جلد.\n" +
            "خانه وقتی آرام‌تر می‌شود که چند خط با صدای معمولی خوانده شود.\n" +
            "کتابخانهٔ شخصی از همین صفحه‌ها ساخته می‌شود: صفحه به صفحه، روز به روز.\n" +
            "اگر جمله را دوباره بخوانید، معنای تازه‌ای پیدا می‌کند.\n" +
            "تمرین امروز ساده است: یک جمله را برای فردا علامت بگذارید.\n" +
            "مطالعه فقط جمع‌کردن اطلاعات نیست؛ تمرین ادب و صبر است.\n" +
            "پایان این صفحه آغاز صفحه بعد است؛ نفس بکشید و ورق بزنید."

    companion object {
        private const val READING_MINUTE_PER_PAGE = 1
        private const val FONT_DEFAULT_SP = 16
        private const val FONT_MIN_SP = 14
        private const val FONT_MAX_SP = 28
    }
}
