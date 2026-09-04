package ir.farhangi.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.semantics.Role
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

@Composable
fun HubTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = FarhangiSize.touchTargetMin)
            .clickable(role = Role.Button, onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(FarhangiSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(FarhangiSize.iconLarge),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
