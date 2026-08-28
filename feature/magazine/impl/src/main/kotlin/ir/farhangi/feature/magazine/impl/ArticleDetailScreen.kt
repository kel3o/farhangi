package ir.farhangi.feature.magazine.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.formatPublishedDate
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.ArticleCoverImage
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

private const val DETAIL_COVER_ASPECT = 16f / 9f

@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onSaveClick: () -> Unit,
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ArticleCoverImage(
                    coverUrl = article.coverUrl,
                    title = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(FarhangiSize.coverDetailWidth)
                        .aspectRatio(DETAIL_COVER_ASPECT),
                )
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "دسته‌بندی: ${article.category.persianLabel()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "تاریخ انتشار: ${formatPublishedDate(article.publishedAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = article.body.ifBlank { article.summary },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = FarhangiSize.touchTargetMin),
                ) {
                    Text(if (article.isSaved) "حذف از ذخیره‌ها" else "ذخیره")
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
