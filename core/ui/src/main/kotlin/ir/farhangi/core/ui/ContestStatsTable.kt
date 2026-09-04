package ir.farhangi.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.formatDurationClock
import ir.farhangi.core.model.toPersianDigits

@Composable
fun ContestStatsTable(
    contest: Contest,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        Column(modifier = Modifier.padding(vertical = FarhangiSpacing.xs)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = FarhangiSpacing.xs),
            ) {
                StatCell(label = "سؤال", value = contest.questionCount.toPersianDigits())
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                StatCell(label = "شرکت‌کننده", value = contest.participantCount.toPersianDigits())
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                StatCell(label = "زمان", value = formatDurationClock(contest.durationSeconds))
            }
            contest.userScorePercent?.let { score ->
                HorizontalDivider(modifier = Modifier.padding(top = FarhangiSpacing.xs))
                Text(
                    text = "نتیجه شما: ${score.toPersianDigits()}٪",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = FarhangiSpacing.sm,
                            vertical = FarhangiSpacing.xs,
                        ),
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatCell(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .heightIn(min = FarhangiSize.contestStatCellMin)
            .padding(horizontal = FarhangiSpacing.xxs, vertical = FarhangiSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
