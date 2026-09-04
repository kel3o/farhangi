package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun StudioItemList(
    title: String,
    uiState: StudioListUiState,
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    extraActionLabel: String? = null,
    onExtraAction: ((String) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        StudioListUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is StudioListUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is StudioListUiState.Success -> {
            var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = FarhangiSpacing.md,
                    end = FarhangiSpacing.md,
                    top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                    bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
                ),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                item {
                    Text(title, style = MaterialTheme.typography.headlineSmall)
                }
                item {
                    Button(
                        onClick = onAdd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("افزودن مورد جدید")
                    }
                }
                if (uiState.items.isEmpty()) {
                    item {
                        EmptyState("موردی نیست", "هنوز موردی ساخته نشده است.")
                    }
                } else {
                    items(uiState.items, key = { it.id }) { item ->
                        StudioItemRow(
                            item = item,
                            extraActionLabel = extraActionLabel,
                            onOpen = { onOpen(item.id) },
                            onEdit = { onEdit(item.id) },
                            onDelete = { pendingDeleteId = item.id },
                            onExtraAction = onExtraAction?.let { action -> { action(item.id) } },
                        )
                    }
                }
                item {
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("بازگشت")
                    }
                }
            }
            pendingDeleteId?.let { id ->
                AlertDialog(
                    onDismissRequest = { pendingDeleteId = null },
                    title = { Text("حذف مورد") },
                    text = { Text("این مورد حذف شود؟") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDelete(id)
                                pendingDeleteId = null
                            },
                        ) { Text("حذف") }
                    },
                    dismissButton = {
                        TextButton(onClick = { pendingDeleteId = null }) { Text("انصراف") }
                    },
                )
            }
        }
    }
}

@Composable
private fun StudioItemRow(
    item: StudioListItem,
    extraActionLabel: String?,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onExtraAction: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        Column(
            modifier = Modifier.padding(FarhangiSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = FarhangiSize.touchTargetMin)
                    .clickable(role = Role.Button, onClick = onOpen),
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.heightIn(min = FarhangiSize.touchTargetMin),
                ) { Text("ویرایش") }
                if (extraActionLabel != null && onExtraAction != null) {
                    OutlinedButton(
                        onClick = onExtraAction,
                        modifier = Modifier.heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text(extraActionLabel) }
                }
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.heightIn(min = FarhangiSize.touchTargetMin),
                ) { Text("حذف") }
            }
        }
    }
}
