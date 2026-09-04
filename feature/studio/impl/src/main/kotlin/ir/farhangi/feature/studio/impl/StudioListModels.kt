package ir.farhangi.feature.studio.impl

data class StudioListItem(
    val id: String,
    val title: String,
    val subtitle: String,
)

sealed interface StudioListUiState {
    data object Loading : StudioListUiState
    data class Success(val items: List<StudioListItem>) : StudioListUiState
    data class Error(val message: String) : StudioListUiState
}
