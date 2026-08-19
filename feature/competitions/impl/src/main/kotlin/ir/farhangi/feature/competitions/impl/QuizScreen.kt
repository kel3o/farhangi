package ir.farhangi.feature.competitions.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

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
        is QuizUiState.Submitted -> Column(
            modifier.fillMaxSize().padding(FarhangiSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
        ) {
            Text("نتیجه آزمون", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "${uiState.result.correctCount} از ${uiState.result.totalCount} صحیح · ${uiState.result.percent} درصد",
                style = MaterialTheme.typography.titleMedium,
            )
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
            ) { Text("بازگشت") }
        }
        is QuizUiState.Ready -> {
            val question = uiState.questions[uiState.currentIndex]
            val selected = uiState.answers[question.id]
            Column(
                modifier.fillMaxSize().padding(FarhangiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(uiState.contestTitle, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "سؤال ${uiState.currentIndex + 1} از ${uiState.questions.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(question.prompt, style = MaterialTheme.typography.titleLarge)
                question.options.forEachIndexed { index, option ->
                    androidx.compose.foundation.layout.Row(
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
                        RadioButton(selected = selected == index, onClick = { onSelectOption(question.id, index) })
                        Text(option, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = FarhangiSpacing.sm))
                    }
                }
                val isLast = uiState.currentIndex == uiState.questions.lastIndex
                Button(
                    onClick = if (isLast) onSubmit else onNext,
                    enabled = selected != null,
                    modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                ) {
                    Text(if (isLast) "ثبت پاسخ‌ها" else "سؤال بعد")
                }
            }
        }
    }
}
