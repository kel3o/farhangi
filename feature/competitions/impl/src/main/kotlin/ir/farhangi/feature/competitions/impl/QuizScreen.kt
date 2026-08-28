package ir.farhangi.feature.competitions.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.formatDurationClock
import ir.farhangi.core.model.formatRemainingDaysAndMinutes
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.ReadingBackdrop
import kotlinx.datetime.Clock

@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onSelectOption: (String, Int) -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        QuizUiState.Loading -> LoadingState(modifier)
        is QuizUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is QuizUiState.Submitted -> SubmittedContent(
            uiState = uiState,
            onBack = onBack,
            modifier = modifier,
        )
        is QuizUiState.Ready -> ReadyContent(
            uiState = uiState,
            onSelectOption = onSelectOption,
            onNext = onNext,
            onSubmit = onSubmit,
            modifier = modifier,
        )
    }
}

@Composable
private fun ReadyContent(
    uiState: QuizUiState.Ready,
    onSelectOption: (String, Int) -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val question = uiState.questions[uiState.currentIndex]
    val selected = uiState.answers[question.id]
    ReadingBackdrop(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(FarhangiSpacing.lg),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(
                    text = uiState.contestTitle,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "زمان باقی‌مانده: ${formatDurationClock(uiState.remainingSeconds)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (uiState.remainingSeconds <= LOW_TIME_SECONDS) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "سؤال ${(uiState.currentIndex + 1).toPersianDigits()} از ${uiState.questions.size.toPersianDigits()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = QUESTION_FONT_SIZE_SP.sp,
                        lineHeight = QUESTION_LINE_HEIGHT_SP.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                question.options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = FarhangiSize.touchTargetMin)
                            .selectable(
                                selected = selected == index,
                                onClick = { onSelectOption(question.id, index) },
                                role = Role.RadioButton,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                    ) {
                        RadioButton(
                            selected = selected == index,
                            onClick = { onSelectOption(question.id, index) },
                        )
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = FarhangiSpacing.sm),
                        )
                    }
                }
            }
            val isLast = uiState.currentIndex == uiState.questions.lastIndex
            Button(
                onClick = if (isLast) onSubmit else onNext,
                enabled = selected != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = FarhangiSpacing.md)
                    .heightIn(min = FarhangiSize.touchTargetMin),
            ) {
                Text(if (isLast) "ثبت پاسخ‌ها" else "سؤال بعد")
            }
        }
    }
}

@Composable
private fun SubmittedContent(
    uiState: QuizUiState.Submitted,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimeoutDialog by rememberSaveable(uiState.timedOut) { mutableStateOf(uiState.timedOut) }
    if (showTimeoutDialog) {
        AlertDialog(
            onDismissRequest = { showTimeoutDialog = false },
            title = { Text("زمان تمام شد") },
            text = { Text("زمان شما به اتمام رسید. پاسخ‌های ثبت‌شده تا این لحظه محاسبه شد و سؤال‌های بی‌پاسخ امتیازی نگرفتند.") },
            confirmButton = {
                TextButton(onClick = { showTimeoutDialog = false }) { Text("متوجه شدم") }
            },
        )
    }
    val result = uiState.result
    val remaining = result.endsAt?.let { formatRemainingDaysAndMinutes(it, Clock.System.now()) } ?: "—"
    val rankText = result.rank?.toPersianDigits() ?: "—"
    Column(
        modifier.fillMaxSize().padding(FarhangiSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
    ) {
        Text("نتیجه آزمون", style = MaterialTheme.typography.headlineSmall)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = FarhangiSpacing.md, vertical = FarhangiSpacing.sm),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                ResultRow(
                    label = "تعداد پاسخ درست",
                    value = "${result.correctCount.toPersianDigits()} از ${result.totalCount.toPersianDigits()}",
                )
                HorizontalDivider()
                ResultRow(
                    label = "درصد شما",
                    value = "${result.percent.toPersianDigits()}٪",
                )
                HorizontalDivider()
                ResultRow(
                    label = "رتبه شما",
                    value = rankText,
                )
                HorizontalDivider()
                ResultRow(
                    label = "امتیاز دریافتی شما",
                    value = "${result.pointsAwarded.toPersianDigits()} امتیاز",
                )
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FarhangiSize.touchTargetMin)
                        .padding(vertical = FarhangiSpacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
                ) {
                    Text(
                        text = "مدت زمان پایان مسابقه",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = remaining,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
        ) { Text("بازگشت") }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = FarhangiSize.touchTargetMin)
            .padding(vertical = FarhangiSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

private const val LOW_TIME_SECONDS = 30
private const val QUESTION_FONT_SIZE_SP = 20
private const val QUESTION_LINE_HEIGHT_SP = 32
