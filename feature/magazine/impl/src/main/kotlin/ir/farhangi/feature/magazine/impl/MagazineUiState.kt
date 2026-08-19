package ir.farhangi.feature.magazine.impl

import ir.farhangi.core.model.Article
import ir.farhangi.core.model.MagazineCategory

sealed interface MagazineUiState {
    data object Loading : MagazineUiState
    data class Success(
        val articles: List<Article>,
        val selectedCategory: MagazineCategory?,
        val availableCategories: List<MagazineCategory>,
    ) : MagazineUiState
    data class Error(val message: String) : MagazineUiState
}

sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState
    data class Success(val article: Article) : ArticleDetailUiState
    data class Error(val message: String) : ArticleDetailUiState
}
