package ir.farhangi.feature.profile.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSignOut: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ProfileUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        ProfileUiState.SignedOut -> EmptyState(
            title = "وارد نشده‌اید",
            message = "برای مشاهده پروفایل وارد شوید.",
            modifier = modifier.padding(contentPadding),
        )
        is ProfileUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is ProfileUiState.Success -> {
            val profile = uiState.profile
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(FarhangiSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
            ) {
                Surface(
                    modifier = Modifier
                        .size(FarhangiSize.avatar)
                        .semantics { contentDescription = "آواتار ${profile.displayName}" },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = profile.displayName.take(1),
                        modifier = Modifier.padding(FarhangiSpacing.lg),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                Text(profile.displayName, style = MaterialTheme.typography.headlineSmall)
                Text(
                    profile.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HorizontalDivider()
                Text("آمار مطالعه", style = MaterialTheme.typography.titleMedium)
                Text("کتاب‌های خوانده‌شده: ${profile.booksRead}", style = MaterialTheme.typography.bodyLarge)
                Text("دوره‌های تکمیل‌شده: ${profile.coursesCompleted}", style = MaterialTheme.typography.bodyLarge)
                Text("رشته مطالعه: ${profile.readingStreakDays} روز", style = MaterialTheme.typography.bodyLarge)
                HorizontalDivider()
                Text("تنظیمات", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "تم پیش‌فرض: روز (روشن)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!uiState.lastPhone.isNullOrBlank()) {
                    Text(
                        text = "آخرین شماره ذخیره‌شده: ${uiState.lastPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FarhangiSize.touchTargetMin)
                        .semantics { contentDescription = "خروج از حساب" },
                ) {
                    Text("خروج از حساب")
                }
            }
        }
    }
}
