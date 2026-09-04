package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.ContestParticipant
import ir.farhangi.core.model.contestAudienceLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.SectionHeader

@Composable
fun StudioContestStatsScreen(
    uiState: ContestStatsUiState,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ContestStatsUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is ContestStatsUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is ContestStatsUiState.Success -> {
            val report = uiState.report
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = FarhangiSpacing.md,
                    end = FarhangiSpacing.md,
                    top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                    bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
                ),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                item {
                    Text(report.title, style = MaterialTheme.typography.headlineSmall)
                }
                item { SectionHeader(title = "آمار مخاطب") }
                item {
                    StatsSummaryRow(
                        participantCount = report.participantCount,
                        maleCount = report.maleCount,
                        femaleCount = report.femaleCount,
                        unspecifiedCount = report.unspecifiedCount,
                    )
                }
                item { SectionHeader(title = "شرکت‌کننده‌ها") }
                if (report.participants.isEmpty()) {
                    item { EmptyState("موردی نیست", "هنوز کسی در این مسابقه شرکت نکرده است.") }
                } else {
                    items(report.participants, key = { it.userId }) { participant ->
                        ParticipantRow(participant)
                    }
                }
                item {
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
}

@Composable
private fun StatsSummaryRow(
    participantCount: Int,
    maleCount: Int,
    femaleCount: Int,
    unspecifiedCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
        ) {
            Text("شرکت‌کننده: ${participantCount.toPersianDigits()}")
            Text("آقا: ${maleCount.toPersianDigits()}")
            Text("خانم: ${femaleCount.toPersianDigits()}")
            Text("نامشخص: ${unspecifiedCount.toPersianDigits()}")
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: ContestParticipant,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        Column(
            modifier = Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${participant.rank.toPersianDigits()}. ${participant.displayName}",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "${participant.percent.toPersianDigits()}٪",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Text(
                text = "${participant.gender.contestAudienceLabel()} · درست: ${participant.correctCount.toPersianDigits()} از ${participant.totalCount.toPersianDigits()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider()
        }
    }
}
