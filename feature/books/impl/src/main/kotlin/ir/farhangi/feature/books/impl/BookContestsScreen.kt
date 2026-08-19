package ir.farhangi.feature.books.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Contest
import ir.farhangi.core.ui.ContestCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun BookContestsScreen(
    uiState: BookContestsUiState,
    onContestClick: (Contest) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        BookContestsUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is BookContestsUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is BookContestsUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = FarhangiSpacing.md,
                end = FarhangiSpacing.md,
                top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            items(uiState.contests, key = { it.id }) { contest ->
                ContestCard(contest = contest, onClick = { onContestClick(contest) })
            }
        }
    }
}

sealed interface BookContestsUiState {
    data object Loading : BookContestsUiState
    data class Success(val contests: List<Contest>) : BookContestsUiState
    data class Error(val message: String) : BookContestsUiState
}
