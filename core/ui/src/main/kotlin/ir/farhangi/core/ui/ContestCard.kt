package ir.farhangi.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiContestColors
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.persianLabel

private const val CARD_SUMMARY_LINES = 2

@Composable
fun ContestCard(
    contest: Contest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = FarhangiSize.touchTargetMin)
            .clickable(role = Role.Button, onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        Column(
            modifier = Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            Text(
                text = contest.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = contest.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = CARD_SUMMARY_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ContestMetaChip(
                    text = contest.category.persianLabel(),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                ContestMetaChip(
                    text = contest.status.persianLabel(),
                    containerColor = contest.status.statusContainerColor(),
                    contentColor = contest.status.statusContentColor(),
                )
            }
            ContestStatsTable(contest = contest)
        }
    }
}

@Composable
private fun ContestMetaChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = containerColor,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(
                horizontal = FarhangiSpacing.xs,
                vertical = FarhangiSpacing.xxs,
            ),
        )
    }
}

fun ContestStatus.statusContainerColor(): Color = when (this) {
    ContestStatus.LIVE -> FarhangiContestColors.LiveContainer
    ContestStatus.FINISHED -> FarhangiContestColors.FinishedContainer
}

fun ContestStatus.statusContentColor(): Color = when (this) {
    ContestStatus.LIVE -> FarhangiContestColors.OnLive
    ContestStatus.FINISHED -> FarhangiContestColors.OnFinished
}
