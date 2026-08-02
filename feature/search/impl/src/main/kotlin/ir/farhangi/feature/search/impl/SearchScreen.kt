package ir.farhangi.feature.search.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.SearchContentType
import ir.farhangi.core.model.SearchResult
import ir.farhangi.core.ui.LoadingState

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onTypeSelected: (SearchContentType?) -> Unit,
    onResultClick: (SearchResult) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(FarhangiSpacing.md),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "فیلد جستجوی سراسری" },
            label = { Text("جستجو در فرهنگی") },
            singleLine = true,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs)) {
            item {
                FilterChip(
                    selected = uiState.selectedType == null,
                    onClick = { onTypeSelected(null) },
                    label = { Text("همه") },
                )
            }
            items(SearchContentType.entries) { type ->
                FilterChip(
                    selected = uiState.selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(typeLabel(type)) },
                )
            }
        }
        when {
            uiState.isLoading -> LoadingState()
            uiState.errorMessage != null -> Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            uiState.results.isEmpty() && uiState.query.isNotBlank() ->
                Text("نتیجه‌ای یافت نشد", style = MaterialTheme.typography.bodyMedium)
            else -> LazyColumn {
                items(uiState.results, key = { "${it.type}-${it.id}" }) { result ->
                    ListItem(
                        headlineContent = { Text(result.title) },
                        supportingContent = { Text(result.subtitle.ifBlank { typeLabel(result.type) }) },
                        modifier = Modifier.clickable(role = Role.Button) { onResultClick(result) },
                    )
                }
            }
        }
    }
}

private fun typeLabel(type: SearchContentType): String = when (type) {
    SearchContentType.BOOK -> "کتاب"
    SearchContentType.COURSE -> "دوره"
    SearchContentType.ARTICLE -> "مقاله"
    SearchContentType.VIDEO -> "ویدیو"
    SearchContentType.AUDIO -> "صوتی"
}