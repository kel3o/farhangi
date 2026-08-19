package ir.farhangi.feature.courses.impl

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.CourseSection
import ir.farhangi.core.model.LessonContentType
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun LessonScreen(
    uiState: LessonUiState,
    onComplete: () -> Unit,
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
                Text("${section.durationMinutes} دقیقه", style = MaterialTheme.typography.labelMedium)
                if (section.contentType == LessonContentType.VIDEO && !section.aparatUrl.isNullOrBlank()) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                loadUrl(section.aparatUrl.orEmpty())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(FarhangiSize.chartHeight),
                    )
                }
                Text(section.body, style = MaterialTheme.typography.bodyLarge)
                if (!section.isCompleted) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                    ) { Text("علامت به‌عنوان دیده‌شده") }
                } else {
                    Text("این جلسه تکمیل شده است.", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin),
                ) { Text("بازگشت") }
            }
        }
    }
}

sealed interface LessonUiState {
    data object Loading : LessonUiState
    data class Success(val section: CourseSection) : LessonUiState
    data class Error(val message: String) : LessonUiState
}
