package ir.farhangi.feature.studio.impl

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.MagazineCategory
import ir.farhangi.core.model.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

data class StudioArticleDraft(
    val id: String = "",
    val title: String = "",
    val category: MagazineCategory = MagazineCategory.CULTURE,
    val summary: String = "",
    val body: String = "",
    val coverUrl: String? = null,
)

data class StudioArticleEditorUiState(
    val draft: StudioArticleDraft = StudioArticleDraft(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val published: Boolean = false,
)

@HiltViewModel
class StudioArticleEditorViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
    private val studioRepository: StudioRepository,
    private val coverStore: StudioCoverStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudioArticleEditorUiState())
    val uiState: StateFlow<StudioArticleEditorUiState> = _uiState.asStateFlow()

    fun load(articleId: String) {
        if (articleId.isBlank()) {
            _uiState.value = StudioArticleEditorUiState()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = magazineRepository.getArticle(articleId)) {
                is Result.Success -> {
                    val article = result.data
                    _uiState.value = StudioArticleEditorUiState(
                        draft = StudioArticleDraft(
                            id = article.id,
                            title = article.title,
                            category = article.category,
                            summary = article.summary,
                            body = article.body,
                            coverUrl = article.coverUrl,
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

    fun updateDraft(transform: (StudioArticleDraft) -> StudioArticleDraft) {
        _uiState.update { it.copy(draft = transform(it.draft), statusMessage = null) }
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
            val body = draft.body.trim()
            val article = Article(
                id = draft.id,
                title = draft.title.trim(),
                type = MediaType.TEXT,
                category = draft.category,
                summary = draft.summary.trim().ifBlank { body.take(SUMMARY_LIMIT) },
                body = body,
                coverUrl = draft.coverUrl,
                publishedAt = Clock.System.now(),
            )
            when (studioRepository.upsertArticle(article)) {
                is Result.Success -> _uiState.update {
                    it.copy(published = true, statusMessage = ARTICLE_PUBLISHED_MESSAGE)
                }
                is Result.Error -> _uiState.update { it.copy(statusMessage = "خطا در انتشار مطلب") }
                Result.Loading -> Unit
            }
        }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null, published = false) }
    }

    companion object {
        const val ARTICLE_PUBLISHED_MESSAGE = "مطلب مجله منتشر شد"
        private const val SUMMARY_LIMIT = 80
    }
}
