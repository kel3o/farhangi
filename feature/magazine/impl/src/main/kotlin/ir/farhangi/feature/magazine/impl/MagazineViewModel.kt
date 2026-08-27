package ir.farhangi.feature.magazine.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.MagazineCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MagazineViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
    private val engagementRepository: EngagementRepository,
) : ViewModel() {

    private val selectedCategory = MutableStateFlow<MagazineCategory?>(null)
    private val articlesResult = MutableStateFlow<Result<List<Article>>?>(null)

    init {
        refresh()
    }

    val uiState: StateFlow<MagazineUiState> = combine(
        articlesResult.filterNotNull(),
        selectedCategory,
    ) { result, category ->
        when (result) {
            is Result.Success -> {
                val all = result.data
                MagazineUiState.Success(
                    articles = if (category == null) all else all.filter { it.category == category },
                    selectedCategory = category,
                    availableCategories = MagazineCategory.entries.toList(),
                )
            }
            is Result.Error -> MagazineUiState.Error(result.exception.message ?: "خطا")
            Result.Loading -> MagazineUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), MagazineUiState.Loading)

    fun selectCategory(category: MagazineCategory?) {
        selectedCategory.value = category
    }

    fun toggleSaved(articleId: String) {
        viewModelScope.launch {
            engagementRepository.toggleSavedArticle(articleId)
            refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            articlesResult.value = magazineRepository.getArticles()
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
