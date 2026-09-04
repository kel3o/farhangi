package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.BookCategories
import ir.farhangi.core.ui.LoadingState

@Composable
fun StudioBookEditorScreen(
    uiState: StudioBookEditorUiState,
    onDraftChange: ((StudioBookDraft) -> StudioBookDraft) -> Unit,
    onToggleCategory: (String) -> Unit,
    onCoverPicked: (android.net.Uri) -> Unit,
    onPublish: () -> Unit,
    onBack: () -> Unit,
    onPublishedConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        LoadingState(modifier)
        return
    }
    val draft = uiState.draft
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Text(
            text = if (draft.id.isBlank()) "کتاب تازه" else "ویرایش کتاب",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "همان فیلدهایی را پر کنید که کاربر در صفحه کتاب می‌بیند. خلاصه به‌صورت خودکار صفحه صفحه می‌شود.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StudioCoverPicker(
            coverUrl = draft.coverUrl,
            title = draft.title,
            onCoverPicked = onCoverPicked,
        )
        OutlinedTextField(
            value = draft.title,
            onValueChange = { value -> onDraftChange { it.copy(title = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("عنوان") },
        )
        OutlinedTextField(
            value = draft.author,
            onValueChange = { value -> onDraftChange { it.copy(author = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("نویسنده") },
        )
        OutlinedTextField(
            value = draft.publisher,
            onValueChange = { value -> onDraftChange { it.copy(publisher = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("ناشر") },
        )
        Text("دسته‌بندی", style = MaterialTheme.typography.titleSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
        ) {
            BookCategories.ALL.forEach { category ->
                FilterChip(
                    selected = category in draft.categories,
                    onClick = { onToggleCategory(category) },
                    label = { Text(category) },
                )
            }
        }
        OutlinedTextField(
            value = draft.description,
            onValueChange = { value -> onDraftChange { it.copy(description = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("توضیح کوتاه") },
            minLines = 3,
        )
        OutlinedTextField(
            value = draft.summary,
            onValueChange = { value -> onDraftChange { it.copy(summary = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("متن خلاصه کتاب") },
            supportingText = { Text("برنامه این متن را صفحه صفحه می‌کند.") },
            minLines = 8,
        )
        OutlinedTextField(
            value = draft.purchaseUrl,
            onValueChange = { value -> onDraftChange { it.copy(purchaseUrl = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("پیوند خرید") },
        )
        if (!uiState.statusMessage.isNullOrBlank() && !uiState.published) {
            Text(uiState.statusMessage, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = onPublish,
            enabled = draft.title.isNotBlank() && !uiState.published,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("انتشار") }
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("بازگشت") }
    }
    if (uiState.published) {
        AlertDialog(
            onDismissRequest = onPublishedConfirmed,
            confirmButton = {
                TextButton(onClick = onPublishedConfirmed) { Text("تأیید") }
            },
            title = { Text("کتاب ثبت شد") },
            text = { Text("کتاب با موفقیت منتشر شد.") },
        )
    }
}
