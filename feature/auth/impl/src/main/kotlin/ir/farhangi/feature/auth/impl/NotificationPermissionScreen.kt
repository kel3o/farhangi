package ir.farhangi.feature.auth.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.icon.FarhangiIcons
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing

@Composable
fun NotificationPermissionScreen(
    onAllow: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = FarhangiIcons.Notifications,
            contentDescription = null,
            modifier = Modifier.size(FarhangiSize.avatar),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "آماده‌سازی اطلاع‌رسانی",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "جهت اطلاع‌رسانی‌های نرم‌افزار لطفاً دسترسی به اعلان‌ها بدهید.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin)
                .semantics { contentDescription = "دسترسی می‌دهم" },
        ) {
            Text("دسترسی می‌دهم")
        }
        OutlinedButton(
            onClick = onLater,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin)
                .semantics { contentDescription = "بعداً دسترسی می‌دهم" },
        ) {
            Text("بعداً دسترسی می‌دهم")
        }
    }
}
