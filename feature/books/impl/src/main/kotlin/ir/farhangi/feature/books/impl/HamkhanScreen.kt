package ir.farhangi.feature.books.impl

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.LeaderboardEntry
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.ScoreBoard
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.SectionHeader

private val TableBorderWidth = 1.dp
private val RankGoldBackground = Color(0xFFFFF4C2)
private val RankSilverBackground = Color(0xFFE8EAED)
private val RankBronzeBackground = Color(0xFFF3E0D0)
private const val COL_RANK_WEIGHT = 0.18f
private const val COL_NAME_WEIGHT = 0.52f
private const val COL_POINTS_WEIGHT = 0.30f
private const val RANK_FIRST = 1
private const val RANK_SECOND = 2
private const val RANK_THIRD = 3

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
                PrimaryTabRow(selectedTabIndex = uiState.period.ordinal) {
                    LeaderboardPeriod.entries.forEach { period ->
                        Tab(
                            selected = uiState.period == period,
                            onClick = { onPeriodSelected(period) },
                            text = {
                                Text(
                                    text = period.persianLabel(),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            },
                            modifier = Modifier.heightIn(min = FarhangiSize.touchTargetMin),
                        )
                    }
                }
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                ) {
                    items(ScoreBoard.entries, key = { it.name }) { board ->
                        FilterChip(
                            selected = uiState.board == board,
                            onClick = { onBoardSelected(board) },
                            label = { Text(board.persianLabel()) },
                        )
                    }
                }
            }
            item { SectionHeader(title = "جدول نفرات برگزیده") }
            item {
                LeaderboardTable(
                    entries = uiState.entries,
                    modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                )
            }
        }
    }
}

@Composable
private fun LeaderboardTable(
    entries: List<LeaderboardEntry>,
    modifier: Modifier = Modifier,
) {
    val outline = MaterialTheme.colorScheme.outlineVariant
    Column(modifier = modifier.fillMaxWidth()) {
        LeaderboardTableHeader(outline = outline)
        entries.forEach { entry ->
            LeaderboardTableRow(
                entry = entry,
                outline = outline,
            )
        }
    }
}

@Composable
private fun LeaderboardTableHeader(
    outline: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = TableBorderWidth, color = outline),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin)
                .padding(horizontal = FarhangiSpacing.xs, vertical = FarhangiSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TableCell(
                text = "رتبه",
                style = MaterialTheme.typography.labelMedium,
                align = TextAlign.Center,
                modifier = Modifier.weight(COL_RANK_WEIGHT),
            )
            TableCell(
                text = "اسم",
                style = MaterialTheme.typography.labelMedium,
                align = TextAlign.Start,
                modifier = Modifier.weight(COL_NAME_WEIGHT),
            )
            TableCell(
                text = "امتیاز",
                style = MaterialTheme.typography.labelMedium,
                align = TextAlign.Center,
                modifier = Modifier.weight(COL_POINTS_WEIGHT),
            )
        }
    }
}

@Composable
private fun LeaderboardTableRow(
    entry: LeaderboardEntry,
    outline: Color,
    modifier: Modifier = Modifier,
) {
    val background = medalBackground(entry.rank)
        ?: MaterialTheme.colorScheme.surface
    val name = buildString {
        append(entry.displayName)
        if (entry.isCurrentUser) append(" (شما)")
    }
    val contentColor = if (entry.isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = TableBorderWidth, color = outline),
        color = background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin)
                .padding(horizontal = FarhangiSpacing.xs, vertical = FarhangiSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TableCell(
                text = entry.rank.toPersianDigits(),
                style = MaterialTheme.typography.bodyMedium,
                align = TextAlign.Center,
                color = contentColor,
                modifier = Modifier.weight(COL_RANK_WEIGHT),
            )
            TableCell(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                align = TextAlign.Start,
                color = contentColor,
                modifier = Modifier.weight(COL_NAME_WEIGHT),
            )
            TableCell(
                text = entry.points.toPersianDigits(),
                style = MaterialTheme.typography.bodyMedium,
                align = TextAlign.Center,
                color = contentColor,
                modifier = Modifier.weight(COL_POINTS_WEIGHT),
            )
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    style: TextStyle,
    align: TextAlign,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = align,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

private fun medalBackground(rank: Int): Color? = when (rank) {
    RANK_FIRST -> RankGoldBackground
    RANK_SECOND -> RankSilverBackground
    RANK_THIRD -> RankBronzeBackground
    else -> null
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
