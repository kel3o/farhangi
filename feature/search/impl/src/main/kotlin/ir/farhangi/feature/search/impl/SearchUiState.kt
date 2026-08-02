package ir.farhangi.feature.search.impl

import ir.farhangi.core.model.SearchContentType
import ir.farhangi.core.model.SearchResult

data class SearchUiState(
    val query: String = "",
    val selectedType: SearchContentType? = null,
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)