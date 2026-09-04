package ir.farhangi.feature.studio.impl

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.ContestCategory
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.QUIZ_OPTION_COUNT
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioContestEditorScreen(
    uiState: StudioContestEditorUiState,
    onDraftChange: ((StudioContestDraft) -> StudioContestDraft) -> Unit,
    onAddQuestion: () -> Unit,
    onRemoveQuestion: (Int) -> Unit,
    onUpdateQuestion: (Int, (StudioQuestionDraft) -> StudioQuestionDraft) -> Unit,
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
            text = if (draft.id.isBlank()) "مسابقه تازه" else "ویرایش مسابقه",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "عنوان، دسته، سؤال‌ها و گزینه صحیح را وارد کنید.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                ContestCategory.entries.forEach { option ->
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
        Text("وضعیت", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
            ContestStatus.entries.forEach { status ->
                FilterChip(
                    selected = draft.status == status,
                    onClick = { onDraftChange { it.copy(status = status) } },
                    label = { Text(status.persianLabel()) },
                )
            }
        }
        OutlinedTextField(
            value = draft.summary,
            onValueChange = { value -> onDraftChange { it.copy(summary = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("توضیح کوتاه") },
            minLines = 4,
        )
        OutlinedTextField(
            value = draft.durationMinutes,
            onValueChange = { value ->
                onDraftChange { it.copy(durationMinutes = value.filter { char -> char.isDigit() }) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("مدت پاسخ (دقیقه)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            value = draft.endsInDays,
            onValueChange = { value ->
                onDraftChange { it.copy(endsInDays = value.filter { char -> char.isDigit() }) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("پس از چند روز تمام شود") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Text("آیا مسابقه «منبع» دارد؟", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
            FilterChip(
                selected = draft.hasSource,
                onClick = { onDraftChange { it.copy(hasSource = true) } },
                label = { Text("بله") },
            )
            FilterChip(
                selected = !draft.hasSource,
                onClick = { onDraftChange { it.copy(hasSource = false) } },
                label = { Text("خیر") },
            )
        }
        if (draft.hasSource) {
            OutlinedTextField(
                value = draft.sourceUrl,
                onValueChange = { value -> onDraftChange { it.copy(sourceUrl = value) } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("پیوند منبع") },
                supportingText = { Text("پیوند وب یا شناسه کتاب مانند book-17") },
            )
        }
        Text("سؤال‌ها", style = MaterialTheme.typography.titleMedium)
        draft.questions.forEachIndexed { index, question ->
            QuizQuestionEditor(
                index = index,
                question = question,
                canRemove = draft.questions.size > 1,
                onChange = { transform -> onUpdateQuestion(index, transform) },
                onRemove = { onRemoveQuestion(index) },
            )
        }
        OutlinedButton(
            onClick = onAddQuestion,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("افزودن سؤال") }
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
            title = { Text("مسابقه ثبت شد") },
            text = { Text("مسابقه با موفقیت ثبت شد.") },
        )
    }
}

@Composable
private fun QuizQuestionEditor(
    index: Int,
    question: StudioQuestionDraft,
    canRemove: Boolean,
    onChange: ((StudioQuestionDraft) -> StudioQuestionDraft) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        Text("سؤال ${(index + 1).toPersianDigits()}", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = question.prompt,
            onValueChange = { value -> onChange { it.copy(prompt = value) } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("صورت سؤال") },
            minLines = 2,
        )
        question.options.take(QUIZ_OPTION_COUNT).forEachIndexed { optionIndex, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            ) {
                RadioButton(
                    selected = question.correctIndex == optionIndex,
                    onClick = { onChange { it.copy(correctIndex = optionIndex) } },
                )
                OutlinedTextField(
                    value = option,
                    onValueChange = { value ->
                        onChange { current ->
                            current.copy(
                                options = current.options.mapIndexed { i, existing ->
                                    if (i == optionIndex) value else existing
                                },
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("گزینه ${(optionIndex + 1).toPersianDigits()}") },
                    supportingText = {
                        if (question.correctIndex == optionIndex) Text("گزینه صحیح")
                    },
                )
            }
        }
        if (canRemove) {
            TextButton(onClick = onRemove) { Text("حذف سؤال") }
        }
    }
}
