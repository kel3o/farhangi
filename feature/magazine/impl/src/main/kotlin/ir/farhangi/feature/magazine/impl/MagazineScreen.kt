package ir.farhangi.feature.magazine.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.MagazineCategory
import ir.farhangi.core.model.persianLabel
import ir.farhangi.core.ui.ArticleCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun MagazineScreen(
    uiState: MagazineUiState,
    onArticleClick: (Article) -> Unit,
    onSaveClick: (Article) -> Unit,
    onCategorySelected: (MagazineCategory?) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        MagazineUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is MagazineUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is MagazineUiState.Success -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding()),
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.xs),
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("همه") },
                    )
                }
                items(uiState.availableCategories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.persianLabel()) },
                    )
                }
            }
            if (uiState.articles.isEmpty()) {
                EmptyState(
                    title = "موردی نیست",
                    message = "در این دسته محتوایی یافت نشد.",
                    modifier = Modifier.padding(FarhangiSpacing.lg),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = FarhangiSpacing.md,
                        end = FarhangiSpacing.md,
                        top = FarhangiSpacing.sm,
                        bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
                    ),
                    verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
                ) {
                    items(uiState.articles, key = { it.id }) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) },
                            onSaveClick = { onSaveClick(article) },
                        )
                    }
                }
            }
        }
    }
}
