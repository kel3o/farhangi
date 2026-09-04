package ir.farhangi.feature.competitions.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.ContestCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun CompetitionsScreen(
    uiState: CompetitionsUiState,
    onContestClick: (Contest) -> Unit,
    onCategorySelected: (ContestCategory?) -> Unit,
    onStatusSelected: (ContestStatus?) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CompetitionsUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is CompetitionsUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is CompetitionsUiState.Success -> Column(
            modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("همه موضوع‌ها") },
                    )
                }
                items(ContestCategory.entries) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.persianLabel()) },
                    )
                }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = FarhangiSpacing.md, vertical = FarhangiSpacing.xs),
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedStatus == null,
                        onClick = { onStatusSelected(null) },
                        label = { Text("همه وضعیت‌ها") },
                    )
                }
                items(ContestStatus.entries) { status ->
                    FilterChip(
                        selected = uiState.selectedStatus == status,
                        onClick = { onStatusSelected(status) },
                        label = { Text(status.persianLabel()) },
                    )
                }
            }
            if (uiState.contests.isEmpty()) {
                EmptyState("موردی نیست", "مسابقه‌ای با این فیلتر نیست.", Modifier.padding(FarhangiSpacing.lg))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = FarhangiSpacing.md,
                        end = FarhangiSpacing.md,
                        top = FarhangiSpacing.sm,
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
    }
}
