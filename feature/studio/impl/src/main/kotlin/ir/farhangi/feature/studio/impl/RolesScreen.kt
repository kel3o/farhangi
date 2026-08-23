package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.StaffMember
import ir.farhangi.core.model.UserRole
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

sealed interface RolesUiState {
    data object Loading : RolesUiState
    data class Success(val staff: List<StaffMember>) : RolesUiState
    data class Error(val message: String) : RolesUiState
}

@Composable
fun RolesScreen(
    uiState: RolesUiState,
    onRoleChange: (String, UserRole) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        RolesUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is RolesUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is RolesUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = FarhangiSpacing.md,
                end = FarhangiSpacing.md,
                top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            item { Text("مدیریت سمت‌ها", style = MaterialTheme.typography.headlineSmall) }
            items(uiState.staff, key = { it.id }) { member ->
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(FarhangiSpacing.md), verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                        Text(member.displayName, style = MaterialTheme.typography.titleSmall)
                        Text(member.phone.toPersianDigits(), style = MaterialTheme.typography.bodySmall)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                            items(UserRole.entries) { role ->
                                FilterChip(
                                    selected = member.role == role,
                                    onClick = { onRoleChange(member.id, role) },
                                    label = { Text(role.persianLabel()) },
                                )
                            }
                        }
                    }
                }
            }
            item {
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin)) {
                    Text("بازگشت")
                }
            }
        }
    }
}
