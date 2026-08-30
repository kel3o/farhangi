package ir.farhangi.feature.profile.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Gender
import ir.farhangi.core.model.canAccessOrgInbox
import ir.farhangi.core.model.canEditContent
import ir.farhangi.core.model.canManageRoles
import ir.farhangi.core.model.canViewReports
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.HonorsRow
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
    onSaveProfile: (String, Gender, Int) -> Unit,
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
            var showEditDialog by rememberSaveable { mutableStateOf(false) }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(FarhangiSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
            ) {
                Text(profile.displayName, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "ویرایش پروفایل",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .heightIn(min = FarhangiSize.touchTargetMin)
                        .clickable { showEditDialog = true }
                        .semantics { contentDescription = "ویرایش پروفایل" }
                        .padding(vertical = FarhangiSpacing.xs),
                )
                PointsSummaryCard(
                    points = uiState.points,
                    weeklyRank = null,
                    readingMinutes = uiState.points.reading,
                )
                if (uiState.trophies.isNotEmpty()) {
                    Text("افتخارات شما", style = MaterialTheme.typography.titleMedium)
                    HonorsRow(
                        trophies = uiState.trophies,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                HorizontalDivider()
                Text("آمار مطالعه", style = MaterialTheme.typography.titleMedium)
                Text(
                    "کتاب‌های خوانده‌شده: ${profile.booksRead.toPersianDigits()}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    "دوره‌های تکمیل‌شده: ${profile.coursesCompleted.toPersianDigits()}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    "رشته مطالعه: ${profile.readingStreakDays.toPersianDigits()} روز",
                    style = MaterialTheme.typography.bodyLarge,
                )
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
            if (showEditDialog) {
                EditProfileDialog(
                    initialName = profile.displayName,
                    initialGender = profile.gender,
                    initialAge = profile.age,
                    onDismiss = { showEditDialog = false },
                    onSave = { name, gender, age ->
                        onSaveProfile(name, gender, age)
                        showEditDialog = false
                    },
                )
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialGender: Gender?,
    initialAge: Int?,
    onDismiss: () -> Unit,
    onSave: (String, Gender, Int) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var gender by rememberSaveable { mutableStateOf(initialGender ?: Gender.MALE) }
    var ageInput by rememberSaveable { mutableStateOf(initialAge?.toString().orEmpty()) }
    val age = ageInput.toIntOrNull()
    val canSave = name.isNotBlank() && age != null && age in AGE_MIN..AGE_MAX
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ویرایش پروفایل") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام و نام خانوادگی") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("جنسیت", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
                    FilterChip(
                        selected = gender == Gender.MALE,
                        onClick = { gender = Gender.MALE },
                        label = { Text(Gender.MALE.persianLabel()) },
                    )
                    FilterChip(
                        selected = gender == Gender.FEMALE,
                        onClick = { gender = Gender.FEMALE },
                        label = { Text(Gender.FEMALE.persianLabel()) },
                    )
                }
                OutlinedTextField(
                    value = ageInput,
                    onValueChange = { ageInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("سن") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAge = age ?: return@TextButton
                    onSave(name.trim(), gender, parsedAge)
                },
                enabled = canSave,
            ) { Text("ذخیره") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        },
    )
}

private const val AGE_MIN = 1
private const val AGE_MAX = 120
