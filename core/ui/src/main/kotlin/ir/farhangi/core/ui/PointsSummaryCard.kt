package ir.farhangi.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.PointsBreakdown

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
            modifier = Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            Text(
                text = "امتیاز فرهنگی شما",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = points.total.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = weeklyRank?.let { "رتبه این هفته: $it" } ?: "هنوز در جدول هفته نیستید",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "مطالعه این هفته: $readingMinutes دقیقه",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.md)) {
                PointChip(label = "مطالعه", value = points.reading)
                PointChip(label = "آموزش", value = points.courses)
                PointChip(label = "مسابقه", value = points.contests)
                PointChip(label = "مجله", value = points.magazine)
            }
        }
    }
}

@Composable
private fun PointChip(label: String, value: Int) {
    Column {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}
