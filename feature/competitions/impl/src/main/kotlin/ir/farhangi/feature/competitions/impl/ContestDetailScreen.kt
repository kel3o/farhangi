package ir.farhangi.feature.competitions.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.ContestStatsTable
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.ExpandableBodyText
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.statusContainerColor
import ir.farhangi.core.ui.statusContentColor

@Composable
fun ContestDetailScreen(
    uiState: ContestDetailUiState,
    onStartQuiz: () -> Unit,
    onOpenBookSource: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ContestDetailUiState.Loading -> LoadingState(modifier)
        is ContestDetailUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is ContestDetailUiState.Success -> {
            ContestDetailContent(
                contest = uiState.contest,
                onStartQuiz = onStartQuiz,
                onOpenBookSource = onOpenBookSource,
                onBack = onBack,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ContestDetailContent(
    contest: Contest,
    onStartQuiz: () -> Unit,
    onOpenBookSource: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var descriptionExpanded by remember(contest.id) { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm)) {
            Text(
                text = contest.title,
                style = MaterialTheme.typography.headlineSmall,
            )
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = contest.status.statusContainerColor(),
            ) {
                Text(
                    text = contest.status.persianLabel(),
                    style = MaterialTheme.typography.labelMedium,
                    color = contest.status.statusContentColor(),
                    modifier = Modifier.padding(
                        horizontal = FarhangiSpacing.sm,
                        vertical = FarhangiSpacing.xxs,
                    ),
                )
            }
        }
        ExpandableBodyText(
            text = contest.summary,
            expanded = descriptionExpanded,
            onToggle = { descriptionExpanded = !descriptionExpanded },
            collapsedLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Text(
                text = contest.category.persianLabel(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(
                    horizontal = FarhangiSpacing.sm,
                    vertical = FarhangiSpacing.xs,
                ),
            )
        }
        ContestStatsTable(contest = contest)
        if (contest.hasSource) {
            OutlinedButton(
                onClick = {
                    val bookId = contest.sourceBookId
                    if (bookId != null) {
                        onOpenBookSource(bookId)
                    } else {
                        contest.sourceUrl?.let { url ->
                            runCatching { uriHandler.openUri(url) }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = FarhangiSize.touchTargetMin),
            ) { Text("منبع مسابقه") }
        }
        if (contest.status == ContestStatus.LIVE) {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = FarhangiSize.touchTargetMin),
            ) { Text("شروع مسابقه") }
        }
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("بازگشت") }
    }
}
