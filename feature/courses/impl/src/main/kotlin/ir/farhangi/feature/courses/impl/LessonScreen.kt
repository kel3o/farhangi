package ir.farhangi.feature.courses.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.R as UiR

private const val VIDEO_PLACEHOLDER_ASPECT = 16f / 9f

@Composable
fun LessonScreen(
    uiState: LessonUiState,
    onToggleCompleted: () -> Unit,
    onPreviousLesson: () -> Unit,
    onNextLesson: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        LessonUiState.Loading -> LoadingState(modifier)
        is LessonUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is LessonUiState.Success -> {
            val section = uiState.section
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(FarhangiSpacing.md),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(section.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "${section.durationMinutes.toPersianDigits()} دقیقه",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
                Text(section.body, style = MaterialTheme.typography.bodyLarge)
                Button(
                    onClick = onToggleCompleted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FarhangiSize.touchTargetMin),
                ) {
                    Text(
                        if (section.isCompleted) {
                            "لغو علامت دیده‌شده"
                        } else {
                            "علامت به‌عنوان دیده‌شده"
                        },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
                ) {
                    FilledTonalButton(
                        onClick = onPreviousLesson,
                        enabled = uiState.hasPrevious,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("جلسه قبل")
                    }
                    FilledTonalButton(
                        onClick = onNextLesson,
                        enabled = uiState.hasNext,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = FarhangiSize.touchTargetMin),
                    ) {
                        Text("جلسه بعد")
                    }
                }
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

sealed interface LessonUiState {
    data object Loading : LessonUiState
    data class Success(
        val section: ir.farhangi.core.model.CourseSection,
        val hasPrevious: Boolean,
        val hasNext: Boolean,
        val previousSectionId: String? = null,
        val nextSectionId: String? = null,
    ) : LessonUiState
    data class Error(val message: String) : LessonUiState
}
