package ir.farhangi.feature.studio.impl

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.CourseCategories
import ir.farhangi.core.model.CourseType
import ir.farhangi.core.model.LessonContentType
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioCourseEditorScreen(
    uiState: StudioCourseEditorUiState,
    onDraftChange: ((StudioCourseDraft) -> StudioCourseDraft) -> Unit,
    onCoverPicked: (Uri) -> Unit,
    onAddSection: () -> Unit,
    onRemoveSection: (Int) -> Unit,
    onUpdateSection: (Int, (StudioSectionDraft) -> StudioSectionDraft) -> Unit,
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
    var levelMenuOpen by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Text(
            text = if (draft.id.isBlank()) "آموزش تازه" else "ویرایش آموزش",
            style = MaterialTheme.typography.headlineSmall,
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
            value = draft.instructor,
            onValueChange = { value -> onDraftChange { it.copy(instructor = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("مدرس") },
        )
        ExposedDropdownMenuBox(
            expanded = categoryMenuOpen,
            onExpandedChange = { categoryMenuOpen = it },
        ) {
            OutlinedTextField(
                value = draft.category,
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
                CourseCategories.ALL.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onDraftChange { it.copy(category = option) }
                            categoryMenuOpen = false
                        },
                    )
                }
            }
        }
        Text("نوع دوره", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
            CourseType.entries.forEach { type ->
                FilterChip(
                    selected = draft.type == type,
                    onClick = { onDraftChange { it.copy(type = type) } },
                    label = { Text(type.persianLabel()) },
                )
            }
        }
        ExposedDropdownMenuBox(
            expanded = levelMenuOpen,
            onExpandedChange = { levelMenuOpen = it },
        ) {
            OutlinedTextField(
                value = draft.level,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                label = { Text("سطح") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelMenuOpen) },
            )
            ExposedDropdownMenu(
                expanded = levelMenuOpen,
                onDismissRequest = { levelMenuOpen = false },
            ) {
                listOf(Course.LEVEL_BEGINNER, Course.LEVEL_INTERMEDIATE, Course.LEVEL_ADVANCED).forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onDraftChange { it.copy(level = option) }
                            levelMenuOpen = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = draft.description,
            onValueChange = { value -> onDraftChange { it.copy(description = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("توضیح") },
            minLines = 4,
        )
        Text("جلسات", style = MaterialTheme.typography.titleMedium)
        draft.sections.forEachIndexed { index, section ->
            Column(verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm)) {
                Text("جلسه ${(index + 1).toPersianDigits()}", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = section.title,
                    onValueChange = { value -> onUpdateSection(index) { it.copy(title = value) } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("عنوان جلسه") },
                )
                OutlinedTextField(
                    value = section.durationMinutes,
                    onValueChange = { value ->
                        onUpdateSection(index) { it.copy(durationMinutes = value.filter { char -> char.isDigit() }) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("مدت (دقیقه)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                    LessonContentType.entries.forEach { type ->
                        FilterChip(
                            selected = section.contentType == type,
                            onClick = { onUpdateSection(index) { it.copy(contentType = type) } },
                            label = { Text(type.persianLabel()) },
                        )
                    }
                }
                if (section.contentType == LessonContentType.VIDEO) {
                    OutlinedTextField(
                        value = section.aparatUrl,
                        onValueChange = { value -> onUpdateSection(index) { it.copy(aparatUrl = value) } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("پیوند آپارات") },
                    )
                }
                OutlinedTextField(
                    value = section.body,
                    onValueChange = { value -> onUpdateSection(index) { it.copy(body = value) } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("متن جلسه") },
                    minLines = 3,
                )
                if (draft.sections.size > 1) {
                    TextButton(onClick = { onRemoveSection(index) }) { Text("حذف جلسه") }
                }
            }
        }
        OutlinedButton(
            onClick = onAddSection,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("افزودن جلسه") }
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
            title = { Text("دوره ثبت شد") },
            text = { Text("آموزش با موفقیت منتشر شد.") },
        )
    }
}
