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
import androidx.compose.material3.OutlinedTextField
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
import ir.farhangi.core.model.STUDIO_CONTEST_CATEGORIES
import ir.farhangi.core.model.fromPersianDigits
import ir.farhangi.core.model.studioLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContestScreen(
    onSubmit: (title: String, category: ContestCategory, body: String, durationMinutes: Int?, sourceUrl: String?) -> Unit,
    onBack: () -> Unit,
    onPublishedConfirmed: () -> Unit,
    statusMessage: String?,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(ContestCategory.BOOK.name) }
    var body by rememberSaveable { mutableStateOf("") }
    var durationMinutes by rememberSaveable { mutableStateOf("") }
    var hasSource by rememberSaveable { mutableStateOf(false) }
    var sourceUrl by rememberSaveable { mutableStateOf("") }
    var categoryMenuOpen by rememberSaveable { mutableStateOf(false) }
    val selectedCategory = ContestCategory.entries.find { it.name == category }
        ?: ContestCategory.BOOK
    val published = statusMessage == CONTEST_PUBLISHED_MESSAGE
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Text("مسابقه تازه", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "عنوان، دسته، توضیح کوتاه و مدت پاسخ را وارد کنید.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("عنوان") },
        )
        ExposedDropdownMenuBox(
            expanded = categoryMenuOpen,
            onExpandedChange = { categoryMenuOpen = it },
        ) {
            OutlinedTextField(
                value = selectedCategory.studioLabel(),
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
                STUDIO_CONTEST_CATEGORIES.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.studioLabel()) },
                        onClick = {
                            category = option.name
                            categoryMenuOpen = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("توضیح کوتاه") },
            minLines = 4,
        )
        OutlinedTextField(
            value = durationMinutes,
            onValueChange = { durationMinutes = it.filter { char -> char.isDigit() } },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("مدت پاسخ (دقیقه)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Text("آیا مسابقه «منبع» دارد؟", style = MaterialTheme.typography.titleSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = hasSource,
                onClick = { hasSource = true },
                label = { Text("بله") },
            )
            FilterChip(
                selected = !hasSource,
                onClick = { hasSource = false },
                label = { Text("خیر") },
            )
        }
        if (hasSource) {
            OutlinedTextField(
                value = sourceUrl,
                onValueChange = { sourceUrl = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("پیوند منبع") },
                supportingText = { Text("پیوند وب یا شناسه کتاب مانند book-17") },
            )
        }
        if (!statusMessage.isNullOrBlank() && !published) {
            Text(statusMessage, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = {
                onSubmit(
                    name,
                    selectedCategory,
                    body,
                    durationMinutes.fromPersianDigits().toIntOrNull(),
                    sourceUrl.trim().takeIf { hasSource && it.isNotEmpty() },
                )
            },
            enabled = name.isNotBlank() && !published,
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
    if (published) {
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

internal const val CONTEST_PUBLISHED_MESSAGE = "مسابقه ثبت شد"
