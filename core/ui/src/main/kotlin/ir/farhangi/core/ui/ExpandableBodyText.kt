package ir.farhangi.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

private const val DEFAULT_COLLAPSED_LINES = 3

@Composable
fun ExpandableBodyText(
    text: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    collapsedLines: Int = DEFAULT_COLLAPSED_LINES,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs)) {
        Box {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (expanded) Int.MAX_VALUE else collapsedLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (!expanded) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.45f to Color.Transparent,
                                    1f to MaterialTheme.colorScheme.surface,
                                ),
                            ),
                        ),
                )
            }
        }
        TextButton(
            onClick = onToggle,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(if (expanded) "کمتر" else "بیشتر")
        }
    }
}
