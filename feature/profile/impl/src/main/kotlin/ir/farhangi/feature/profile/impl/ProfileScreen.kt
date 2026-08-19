package ir.farhangi.feature.profile.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import ir.farhangi.core.model.canAccessOrgInbox
import ir.farhangi.core.model.canEditContent
import ir.farhangi.core.model.canManageRoles
import ir.farhangi.core.model.canViewReports
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.PointsSummaryCard

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSignOut: () -> Unit,
    onStudioClick: () -> Unit,
    onOrgInboxClick: () -> Unit,
    onReportsClick: () -> Unit,
    onRolesClick: () -> Unit,
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
                    .verticalScroll(rememberScrollState())
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
                Text(profile.role.persianLabel(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(
                    profile.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                PointsSummaryCard(
                    points = uiState.points,
                    weeklyRank = null,
                    readingMinutes = uiState.points.reading,
                )
                if (uiState.trophies.isNotEmpty()) {
                    Text("جام‌ها", style = MaterialTheme.typography.titleMedium)
                    uiState.trophies.forEach { trophy ->
                        Text("${trophy.title} · ${trophy.weekOrMonthLabel}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                HorizontalDivider()
                Text("آمار مطالعه", style = MaterialTheme.typography.titleMedium)
                Text("کتاب‌های خوانده‌شده: ${profile.booksRead}", style = MaterialTheme.typography.bodyLarge)
                Text("دوره‌های تکمیل‌شده: ${profile.coursesCompleted}", style = MaterialTheme.typography.bodyLarge)
                Text("رشته مطالعه: ${profile.readingStreakDays} روز", style = MaterialTheme.typography.bodyLarge)
                if (profile.role.canEditContent()) {
                    Button(
                        onClick = onStudioClick,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("استودیوی محتوا") }
                }
                if (profile.role.canAccessOrgInbox()) {
                    Button(
                        onClick = onOrgInboxClick,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("صندوق پیام سازمانی") }
                }
                if (profile.role.canViewReports()) {
                    Button(
                        onClick = onReportsClick,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("گزارش‌ها") }
                }
                if (profile.role.canManageRoles()) {
                    Button(
                        onClick = onRolesClick,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("مدیریت سمت‌ها") }
                }
                HorizontalDivider()
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
