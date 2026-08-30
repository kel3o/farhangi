package ir.farhangi.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.PointsBreakdown
import ir.farhangi.core.model.toPersianDigits

@Composable
fun PointsSummaryCard(
    points: PointsBreakdown,
    weeklyRank: Int?,
    readingMinutes: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FarhangiSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            Text(
                text = "امتیاز فرهنگی شما",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = points.total.toPersianDigits(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = weeklyRank?.let { "رتبه این هفته: ${it.toPersianDigits()}" }
                    ?: "هنوز در جدول هفته نیستید",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "مطالعه این هفته: ${readingMinutes.toPersianDigits()} دقیقه",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            ) {
                PointChip(
                    label = "مطالعه",
                    value = points.reading,
                    icon = FarhangiIcons.Books,
                    modifier = Modifier.weight(1f),
                )
                PointChip(
                    label = "آموزش",
                    value = points.courses,
                    icon = FarhangiIcons.Courses,
                    modifier = Modifier.weight(1f),
                )
                PointChip(
                    label = "مسابقه",
                    value = points.contests,
                    icon = FarhangiIcons.Competitions,
                    modifier = Modifier.weight(1f),
                )
                PointChip(
                    label = "مجله",
                    value = points.magazine,
                    icon = FarhangiIcons.Magazine,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PointChip(
    label: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface.copy(alpha = POINT_CHIP_SURFACE_ALPHA),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = FarhangiSpacing.sm, horizontal = FarhangiSpacing.xxs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(FarhangiSize.iconSmall),
            )
            Text(
                text = value.toPersianDigits(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val POINT_CHIP_SURFACE_ALPHA = 0.45f
