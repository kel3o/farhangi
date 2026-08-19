package ir.farhangi.feature.magazine.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        ArticleDetailUiState.Loading -> LoadingState(modifier)
        is ArticleDetailUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is ArticleDetailUiState.Success -> {
            val article = uiState.article
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(FarhangiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(article.category.persianLabel(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(article.title, style = MaterialTheme.typography.headlineSmall)
                Text(article.body.ifBlank { article.summary }, style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("بازگشت") }
            }
        }
    }
}