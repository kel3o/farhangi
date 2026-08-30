package ir.farhangi.feature.courses.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.CourseSection
import ir.farhangi.core.model.CourseType
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.BookCover
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.ExpandableBodyText
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.R as UiR

private const val DESCRIPTION_COLLAPSED_LINES = 4
private const val PROGRESS_PERCENT_BASE = 100
private const val VIDEO_PLACEHOLDER_ASPECT = 16f / 9f
private val TableBorderWidth = 1.dp

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
            val isPractical = course.type == CourseType.PRACTICAL
            var descriptionExpanded by remember(course.id) { mutableStateOf(false) }
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
                    CourseMetaRow(course = course, isPractical = isPractical)
                }
                item {
                    ExpandableBodyText(
                        text = course.description,
                        expanded = descriptionExpanded,
                        onToggle = { descriptionExpanded = !descriptionExpanded },
                        collapsedLines = DESCRIPTION_COLLAPSED_LINES,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (isPractical) {
                    item {
                        Image(
                            painter = painterResource(UiR.drawable.video_placeholder),
                            contentDescription = "محل قرارگیری ویدیو در آینده",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(VIDEO_PLACEHOLDER_ASPECT)
                                .semantics {
                                    contentDescription = "پیش‌نمایش پخش‌کننده ویدیو"
                                },
                            contentScale = ContentScale.Fit,
                        )
                    }
                    val lessonBody = course.sections.firstOrNull()?.body.orEmpty()
                    if (lessonBody.isNotBlank()) {
                        item {
                            Text(
                                text = lessonBody,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                } else {
                    item {
                        CourseProgressSection(progress = course.progress)
                    }
                    item {
                        Text(
                            text = "جلسات آموزشی",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = FarhangiSpacing.xs),
                            textAlign = TextAlign.Start,
                        )
                    }
                    item {
                        SessionsTableHeader()
                    }
                    itemsIndexed(course.sections, key = { _, section -> section.id }) { index, section ->
                        SessionTableRow(
                            index = index + 1,
                            section = section,
                            onStart = { onOpenLesson(section.id) },
                        )
                    }
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
private fun CourseProgressSection(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val percent = (progress * PROGRESS_PERCENT_BASE).toInt().coerceIn(0, PROGRESS_PERCENT_BASE)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = FarhangiSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        HorizontalDivider()
        Text(
            text = "میزان پیشرفت شما",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = FarhangiSpacing.xs),
        ) {
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .semantics {
                        contentDescription = "پیشرفت دوره ${percent.toPersianDigits()} درصد"
                    },
            )
            if (percent > 0) {
                Text(
                    text = "${percent.toPersianDigits()}٪",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "۰٪",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "۱۰۰٪",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider()
    }
}

@Composable
private fun SessionsTableHeader(modifier: Modifier = Modifier) {
    val outline = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = TableBorderWidth, color = outline)
            .padding(vertical = FarhangiSpacing.xs, horizontal = FarhangiSpacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TableHeaderCell("شماره", Modifier.weight(SESSION_COL_NUMBER), TextAlign.Center)
        TableHeaderCell("عنوان", Modifier.weight(SESSION_COL_TITLE), TextAlign.Start)
        TableHeaderCell("مدت (د)", Modifier.weight(SESSION_COL_DURATION), TextAlign.Center)
        TableHeaderCell("شروع", Modifier.weight(SESSION_COL_ACTION), TextAlign.Center)
    }
}

@Composable
private fun SessionTableRow(
    index: Int,
    section: CourseSection,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outline = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = TableBorderWidth, color = outline)
            .padding(vertical = FarhangiSpacing.xxs, horizontal = FarhangiSpacing.xxs)
            .heightIn(min = FarhangiSize.touchTargetMin),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = index.toPersianDigits(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(SESSION_COL_NUMBER),
        )
        Text(
            text = section.title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(SESSION_COL_TITLE),
        )
        Text(
            text = section.durationMinutes.toPersianDigits(),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(SESSION_COL_DURATION),
        )
        TextButton(
            onClick = onStart,
            modifier = Modifier
                .weight(SESSION_COL_ACTION)
                .semantics {
                    contentDescription = "باز کردن بخش ${section.title}"
                },
        ) {
            Text(
                text = if (section.isCompleted) "مرور" else "شروع",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun TableHeaderCell(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
private fun CourseMetaRow(
    course: Course,
    isPractical: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
    ) {
        if (!isPractical) {
            MetaChip(
                label = "جلسه",
                value = course.sessionCount.toPersianDigits(),
                modifier = Modifier.weight(1f),
            )
        }
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

private const val SESSION_COL_NUMBER = 0.14f
private const val SESSION_COL_TITLE = 0.46f
private const val SESSION_COL_DURATION = 0.18f
private const val SESSION_COL_ACTION = 0.22f
