package ir.farhangi.feature.studio.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.NamedCount
import ir.farhangi.core.model.PlatformReport
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.AudienceBarChart
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.SectionHeader

sealed interface ReportsUiState {
    data object Loading : ReportsUiState
    data class Success(val report: PlatformReport) : ReportsUiState
    data class Error(val message: String) : ReportsUiState
}

@Composable
fun ReportsScreen(
    uiState: ReportsUiState,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ReportsUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is ReportsUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is ReportsUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = FarhangiSpacing.md,
                end = FarhangiSpacing.md,
                top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
        ) {
            item { Text("گزارش مدیرکل", style = MaterialTheme.typography.headlineSmall) }
            item { SectionHeader(title = "مخاطب بخش‌ها") }
            item { AudienceBarChart(items = uiState.report.sectionAudience) }
            item { SectionHeader(title = "پرمخاطب‌ترین کتاب‌ها") }
            items(uiState.report.topBooks) { NamedCountRow(it) }
            item { SectionHeader(title = "پرمخاطب‌ترین آموزش‌ها") }
            items(uiState.report.topCourses) { NamedCountRow(it) }
            item { SectionHeader(title = "پرمخاطب‌ترین مطالب مجله") }
            items(uiState.report.topArticles) { NamedCountRow(it) }
            item {
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth().heightIn(min = FarhangiSize.touchTargetMin)) {
                    Text("بازگشت")
                }
            }
        }
    }
}

@Composable
private fun NamedCountRow(item: NamedCount) {
    Text("${item.name}: ${item.count.toPersianDigits()}", style = MaterialTheme.typography.bodyLarge)
}
