package ir.farhangi.feature.books.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.LoadingState

@Composable
fun BookReaderScreen(
    uiState: ReaderUiState,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleNight: () -> Unit,
    onToggleBookmark: () -> Unit,
    onAddHighlight: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        LoadingState(modifier = modifier)
        return
    }

    val colorScheme = MaterialTheme.colorScheme
    val background = if (uiState.isNightMode) {
        colorScheme.inverseSurface
    } else {
        colorScheme.surface
    }
    val foreground = if (uiState.isNightMode) {
        colorScheme.inverseOnSurface
    } else {
        colorScheme.onSurface
    }
    val secondary = if (uiState.isNightMode) {
        colorScheme.inverseOnSurface.copy(alpha = SECONDARY_ALPHA)
    } else {
        colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .padding(FarhangiSpacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier
                    .heightIn(min = FarhangiSize.touchTargetMin)
                    .semantics { contentDescription = "بازگشت از خواننده" },
            ) { Text("بازگشت") }
            TextButton(
                onClick = onToggleNight,
                modifier = Modifier
                    .heightIn(min = FarhangiSize.touchTargetMin)
                    .semantics {
                        contentDescription = if (uiState.isNightMode) {
                            "فعال‌سازی حالت روز"
                        } else {
                            "فعال‌سازی حالت شب"
                        }
                    },
            ) {
                Text(if (uiState.isNightMode) "حالت روز" else "حالت شب")
            }
        }
        Text(
            text = uiState.bookTitle,
            style = MaterialTheme.typography.titleMedium,
            color = foreground,
        )
        Text(
            text = "صفحه ${uiState.page} از ${uiState.totalPages}",
            style = MaterialTheme.typography.labelMedium,
            color = secondary,
            modifier = Modifier.padding(vertical = FarhangiSpacing.xs),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
        ) {
            TextButton(
                onClick = onToggleBookmark,
                modifier = Modifier
                    .heightIn(min = FarhangiSize.touchTargetMin)
                    .semantics {
                        contentDescription = if (uiState.isBookmarked) {
                            "حذف نشانک"
                        } else {
                            "افزودن نشانک"
                        }
                    },
            ) {
                Text(if (uiState.isBookmarked) "حذف نشانک" else "نشانک")
            }
            TextButton(
                onClick = onAddHighlight,
                modifier = Modifier
                    .heightIn(min = FarhangiSize.touchTargetMin)
                    .semantics { contentDescription = "افزودن هایلایت صفحه" },
            ) {
                Text("هایلایت")
            }
        }
        if (uiState.highlights.isNotEmpty()) {
            Text(
                text = "هایلایت‌ها: ${uiState.highlights.size}",
                style = MaterialTheme.typography.labelMedium,
                color = secondary,
                modifier = Modifier.padding(bottom = FarhangiSpacing.xs),
            )
        }
        Text(
            text = uiState.pageText,
            style = MaterialTheme.typography.bodyLarge,
            color = foreground,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        ) {
            FilledTonalButton(
                onClick = onPrevious,
                enabled = uiState.page > 1,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = FarhangiSize.touchTargetMin),
            ) { Text("قبلی") }
            FilledTonalButton(
                onClick = onNext,
                enabled = uiState.page < uiState.totalPages,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = FarhangiSize.touchTargetMin),
            ) { Text("بعدی") }
        }
    }
}

private const val SECONDARY_ALPHA = 0.7f
