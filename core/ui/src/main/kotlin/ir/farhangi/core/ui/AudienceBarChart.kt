package ir.farhangi.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.NamedCount
import ir.farhangi.core.model.toPersianDigits

@Composable
fun AudienceBarChart(
    items: List<NamedCount>,
    modifier: Modifier = Modifier,
) {
    val maxValue = items.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FarhangiSize.chartHeight),
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        verticalAlignment = Alignment.Bottom,
    ) {
        items.forEach { item ->
            val fraction = item.count.toFloat() / maxValue.toFloat()
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
            ) {
                Text(text = item.count.toPersianDigits(), style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(min = FarhangiSize.barMinWidth)
                            .fillMaxWidth()
                            .fillMaxHeight(fraction)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall,
                            ),
                    )
                }
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                )
            }
        }
    }
}
