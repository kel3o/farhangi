package ir.farhangi.feature.courses.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun CourseDetailScreen(
    uiState: CourseDetailUiState,
    onCompleteSection: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        CourseDetailUiState.Loading -> LoadingState(modifier)
        is CourseDetailUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is CourseDetailUiState.Success -> {
            val course = uiState.course
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(FarhangiSpacing.md),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                item {
                    Text(course.title, style = MaterialTheme.typography.headlineSmall)
                }
                item {
                    Text(course.description, style = MaterialTheme.typography.bodyLarge)
                }
                item {
                    LinearProgressIndicator(
                        progress = { course.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "پیشرفت دوره ${(course.progress * 100).toInt()} درصد"
                            },
                    )
                }
                items(course.sections, key = { it.id }) { section ->
                    ListItem(
                        headlineContent = { Text(section.title) },
                        supportingContent = {
                            Text("${section.durationMinutes} دقیقه")
                        },
                        trailingContent = {
                            if (section.isCompleted) {
                                Text("تکمیل‌شده", color = MaterialTheme.colorScheme.primary)
                            } else {
                                TextButton(
                                    onClick = { onCompleteSection(section.id) },
                                    modifier = Modifier
                                        .heightIn(min = FarhangiSize.touchTargetMin)
                                        .semantics {
                                            contentDescription = "تکمیل بخش ${section.title}"
                                        },
                                ) {
                                    Text("تکمیل")
                                }
                            }
                        },
                    )
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
        }
    }
}
