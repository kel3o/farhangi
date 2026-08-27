package ir.farhangi.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.formatDurationClock
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits

private const val CARD_SUMMARY_LINES = 3

@Composable
fun ContestCard(
    contest: Contest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        Column(
            modifier = Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            Text(text = contest.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = contest.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = CARD_SUMMARY_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                AssistChip(
                    onClick = onClick,
                    label = { Text(contest.category.persianLabel()) },
                )
                AssistChip(
                    onClick = onClick,
                    label = { Text(contest.status.persianLabel()) },
                )
            }
            val result = contest.userScorePercent?.let { "نتیجه شما: ${it.toPersianDigits()} درصد · " }.orEmpty()
            Text(
                text = "$result${contest.questionCount.toPersianDigits()} سؤال · ${contest.participantCount.toPersianDigits()} شرکت‌کننده · ${formatDurationClock(contest.durationSeconds)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
