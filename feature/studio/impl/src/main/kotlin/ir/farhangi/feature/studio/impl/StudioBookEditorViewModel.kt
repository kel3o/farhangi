package ir.farhangi.feature.studio.impl

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.BookCategories
import ir.farhangi.core.model.DEFAULT_BOOK_PURCHASE_URL
import ir.farhangi.core.model.paginateReadingText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudioBookDraft(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val publisher: String = "",
    val coverUrl: String? = null,
    val categories: List<String> = emptyList(),
    val description: String = "",
    val summary: String = "",
    val purchaseUrl: String = "",
)

data class StudioBookEditorUiState(
    val draft: StudioBookDraft = StudioBookDraft(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val published: Boolean = false,
)

@HiltViewModel
class StudioBookEditorViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val studioRepository: StudioRepository,
    private val coverStore: StudioCoverStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudioBookEditorUiState())
    val uiState: StateFlow<StudioBookEditorUiState> = _uiState.asStateFlow()

    fun load(bookId: String) {
        if (bookId.isBlank()) {
            _uiState.value = StudioBookEditorUiState()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = bookRepository.getBook(bookId)) {
                is Result.Success -> {
                    val book = result.data
                    _uiState.value = StudioBookEditorUiState(
                        draft = StudioBookDraft(
                            id = book.id,
                            title = book.title,
                            author = book.author,
                            publisher = book.publisher,
                            coverUrl = book.coverUrl,
                            categories = book.categories,
                            description = book.description,
                            summary = book.pages.joinToString("\n\n"),
                            purchaseUrl = book.purchaseUrl,
                        ),
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, statusMessage = result.exception.message ?: "بارگذاری ناموفق بود")
                }
                Result.Loading -> Unit
            }
        }
    }

    fun updateDraft(transform: (StudioBookDraft) -> StudioBookDraft) {
        _uiState.update { it.copy(draft = transform(it.draft), statusMessage = null) }
    }

    fun toggleCategory(category: String) {
        updateDraft { draft ->
            val selected = if (category in draft.categories) {
                draft.categories - category
            } else {
                draft.categories + category
            }
            draft.copy(categories = selected)
        }
    }

    fun onCoverPicked(uri: Uri) {
        val persisted = coverStore.persist(uri)
        updateDraft { it.copy(coverUrl = persisted) }
    }

    fun publish() {
        val draft = _uiState.value.draft
        if (draft.title.isBlank()) {
            _uiState.update { it.copy(statusMessage = "عنوان را وارد کنید") }
            return
        }
        viewModelScope.launch {
            val pages = paginateReadingText(draft.summary.ifBlank { draft.description })
            val book = Book(
                id = draft.id,
                title = draft.title.trim(),
                author = draft.author.trim().ifBlank { "ویرایشگر" },
                publisher = draft.publisher.trim(),
                coverUrl = draft.coverUrl,
                categories = draft.categories.ifEmpty { listOf(BookCategories.BOOK_SUMMARY) },
                pages = pages,
                totalPages = pages.size,
                description = draft.description.trim(),
                purchaseUrl = draft.purchaseUrl.trim().ifBlank { DEFAULT_BOOK_PURCHASE_URL },
            )
            when (studioRepository.upsertBook(book)) {
                is Result.Success -> _uiState.update {
                    it.copy(published = true, statusMessage = BOOK_PUBLISHED_MESSAGE)
                }
                is Result.Error -> _uiState.update { it.copy(statusMessage = "خطا در انتشار کتاب") }
                Result.Loading -> Unit
            }
        }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null, published = false) }
    }

    companion object {
        const val BOOK_PUBLISHED_MESSAGE = "کتاب منتشر شد"
    }
}
