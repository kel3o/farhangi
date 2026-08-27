package ir.farhangi.feature.courses.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.BookCover
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun CourseDetailScreen(
    uiState: CourseDetailUiState,
    onOpenLesson: (String) -> Unit,
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    BookCover(
                        coverUrl = course.coverUrl,
                        title = course.title,
                        modifier = Modifier.width(FarhangiSize.coverDetailWidth),
                    )
                }
                item {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Text(
                        text = course.instructor.ifBlank { "مدرس اعلام نشده" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Text(
                        text = "دسته‌بندی: ${course.category.ifBlank { "—" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    CourseMetaRow(course = course)
                }
                item {
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    LinearProgressIndicator(
                        progress = { course.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription =
                                    "پیشرفت دوره ${(course.progress * 100).toInt()} درصد"
                            },
                    )
                }
                items(course.sections, key = { it.id }) { section ->
                    ListItem(
                        headlineContent = { Text(section.title) },
                        supportingContent = null,
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                            ) {
                                Text(
                                    text = "${section.durationMinutes.toPersianDigits()} دقیقه",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                TextButton(
                                    onClick = { onOpenLesson(section.id) },
                                    modifier = Modifier
                                        .heightIn(min = FarhangiSize.touchTargetMin)
                                        .semantics {
                                            contentDescription = "باز کردن بخش ${section.title}"
                                        },
                                ) {
                                    Text(if (section.isCompleted) "مرور" else "شروع")
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

@Composable
private fun CourseMetaRow(
    course: Course,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
    ) {
        MetaChip(
            label = "جلسه",
            value = course.sessionCount.toPersianDigits(),
            modifier = Modifier.weight(1f),
        )
        MetaChip(
            label = "مدت",
            value = "${course.totalDurationMinutes.toPersianDigits()} د",
            modifier = Modifier.weight(1f),
        )
        MetaChip(
            label = "سطح",
            value = course.level,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetaChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = FarhangiSpacing.xxs,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = FarhangiSpacing.sm, horizontal = FarhangiSpacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
            )
        }
    }
}
