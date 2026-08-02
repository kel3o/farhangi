package ir.farhangi.feature.books.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Book
import ir.farhangi.core.ui.BookCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun BooksScreen(
    uiState: BooksUiState,
    onBookClick: (Book) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        BooksUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is BooksUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is BooksUiState.Success -> LazyVerticalGrid(
            columns = GridCells.Adaptive(FarhangiSize.coverWidth),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = FarhangiSpacing.md,
                end = FarhangiSpacing.md,
                top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
            ),
            horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
        ) {
            items(uiState.books, key = { it.id }) { book ->
                BookCard(book = book, onClick = { onBookClick(book) })
            }
        }
    }
}