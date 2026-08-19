package ir.farhangi.feature.books.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.LeaderboardEntry
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.ScoreBoard
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.SectionHeader

@Composable
fun HamkhanScreen(
    uiState: HamkhanUiState,
    onPeriodSelected: (LeaderboardPeriod) -> Unit,
    onBoardSelected: (ScoreBoard) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        HamkhanUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is HamkhanUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is HamkhanUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            item {
                Text(
                    text = "هر دقیقه مطالعه امتیاز می‌سازد. ده نفر برتر هفته و ماه جام می‌گیرند.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                )
            }
            item {
                ScrollableTabRow(selectedTabIndex = uiState.period.ordinal) {
                    LeaderboardPeriod.entries.forEach { period ->
                        Tab(
                            selected = uiState.period == period,
                            onClick = { onPeriodSelected(period) },
                            text = { Text(period.persianLabel()) },
                        )
                    }
                }
            }
            item {
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                ) {
                    items(ScoreBoard.entries.size) { index ->
                        val board = ScoreBoard.entries[index]
                        FilterChip(
                            selected = uiState.board == board,
                            onClick = { onBoardSelected(board) },
                            label = { Text(board.persianLabel()) },
                        )
                    }
                }
            }
            item { SectionHeader(title = "ده نفر اول") }
            items(uiState.entries, key = { it.userId }) { entry ->
                LeaderboardRow(entry = entry, modifier = Modifier.padding(horizontal = FarhangiSpacing.md))
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = FarhangiSpacing.xxs),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
    ) {
        Text(
            text = "${entry.rank}. ${entry.displayName}${if (entry.isCurrentUser) " (شما)" else ""}",
            style = MaterialTheme.typography.titleSmall,
            color = if (entry.isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "${entry.points} امتیاز",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

sealed interface HamkhanUiState {
    data object Loading : HamkhanUiState
    data class Success(
        val period: LeaderboardPeriod,
        val board: ScoreBoard,
        val entries: List<LeaderboardEntry>,
    ) : HamkhanUiState
    data class Error(val message: String) : HamkhanUiState
}
