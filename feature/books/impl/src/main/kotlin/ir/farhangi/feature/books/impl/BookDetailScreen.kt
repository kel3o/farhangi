package ir.farhangi.feature.books.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onReadClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        BookDetailUiState.Loading -> LoadingState(modifier)
        is BookDetailUiState.Error -> EmptyState("خطا", uiState.message, modifier)
        is BookDetailUiState.Success -> {
            val book = uiState.book
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(FarhangiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
            ) {
                Text(book.title, style = MaterialTheme.typography.headlineSmall)
                Text(book.author, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${book.totalPages} صفحه", style = MaterialTheme.typography.bodyMedium)
                Text(book.description, style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onReadClick, modifier = Modifier.fillMaxWidth()) {
                    Text("شروع مطالعه")
                }
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("بازگشت")
                }
            }
        }
    }
}