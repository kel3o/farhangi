package ir.farhangi.feature.competitions.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.formatDurationClock
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.ExpandableBodyText
import ir.farhangi.core.ui.LoadingState

@Composable
fun ContestDetailScreen(
    uiState: ContestDetailUiState,
    onStartQuiz: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ContestDetailUiState.Loading -> LoadingState(modifier)
        is ContestDetailUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is ContestDetailUiState.Success -> {
            val contest = uiState.contest
            var descriptionExpanded by remember(contest.id) { mutableStateOf(false) }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(FarhangiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(FarhangiSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
                    ) {
                        Text(
                            text = contest.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = contest.status.persianLabel(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
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
                Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                    AssistChip(onClick = {}, label = { Text(contest.category.persianLabel()) })
                    AssistChip(onClick = {}, label = { Text(contest.status.persianLabel()) })
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(
                        modifier = Modifier.padding(FarhangiSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                    ) {
                        Text(
                            text = "${contest.questionCount.toPersianDigits()} سؤال چهارگزینه‌ای",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = "زمان پاسخ‌گویی: ${formatDurationClock(contest.durationSeconds)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${contest.participantCount.toPersianDigits()} شرکت‌کننده",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        contest.userScorePercent?.let {
                            Text(
                                text = "نتیجه ثبت‌شده شما: ${it.toPersianDigits()} درصد",
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
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
    }
}
