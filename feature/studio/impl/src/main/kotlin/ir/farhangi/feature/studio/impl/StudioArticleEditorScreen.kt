package ir.farhangi.feature.studio.impl

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.MagazineCategory
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioArticleEditorScreen(
    uiState: StudioArticleEditorUiState,
    onDraftChange: ((StudioArticleDraft) -> StudioArticleDraft) -> Unit,
    onCoverPicked: (Uri) -> Unit,
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
    var categoryMenuOpen by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Text(
            text = if (draft.id.isBlank()) "مطلب تازه مجله" else "ویرایش مطلب",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "تصویر، عنوان، دسته و متن همان چیزهایی هستند که در مجله دیده می‌شود.",
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
        ExposedDropdownMenuBox(
            expanded = categoryMenuOpen,
            onExpandedChange = { categoryMenuOpen = it },
        ) {
            OutlinedTextField(
                value = draft.category.persianLabel(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                label = { Text("دسته") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuOpen) },
            )
            ExposedDropdownMenu(
                expanded = categoryMenuOpen,
                onDismissRequest = { categoryMenuOpen = false },
            ) {
                MagazineCategory.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.persianLabel()) },
                        onClick = {
                            onDraftChange { it.copy(category = option) }
                            categoryMenuOpen = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = draft.summary,
            onValueChange = { value -> onDraftChange { it.copy(summary = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("خلاصه کارت") },
            supportingText = { Text("اگر خالی بماند از ابتدای متن ساخته می‌شود.") },
            minLines = 2,
        )
        OutlinedTextField(
            value = draft.body,
            onValueChange = { value -> onDraftChange { it.copy(body = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("متن مطلب") },
            minLines = 8,
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
            title = { Text("مطلب ثبت شد") },
            text = { Text("مطلب مجله با موفقیت منتشر شد.") },
        )
    }
}
