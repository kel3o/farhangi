package ir.farhangi.feature.books.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Book
import ir.farhangi.core.ui.BookCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

private const val LIBRARY_COLUMNS = 3

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onBookClick: (Book) -> Unit,
    onCategorySelected: (String?) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        LibraryUiState.Loading -> LoadingState(modifier.padding(contentPadding))
        is LibraryUiState.Error -> EmptyState("خطا", uiState.message, modifier.padding(contentPadding))
        is LibraryUiState.Success -> Column(modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
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
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category) },
                    )
                }
            }
            if (uiState.books.isEmpty()) {
                EmptyState("موردی نیست", "در این دسته کتابی نیست.", Modifier.padding(FarhangiSpacing.lg))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(LIBRARY_COLUMNS),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = FarhangiSpacing.md,
                        end = FarhangiSpacing.md,
                        top = FarhangiSpacing.sm,
                        bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
                ) {
                    items(uiState.books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book) },
                            modifier = Modifier.fillMaxWidth(),
                            compact = false,
                        )
                    }
                }
            }
        }
    }
}

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Success(
        val books: List<Book>,
        val categories: List<String>,
        val selectedCategory: String?,
    ) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}
