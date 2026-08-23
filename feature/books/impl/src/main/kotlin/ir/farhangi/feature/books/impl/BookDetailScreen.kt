package ir.farhangi.feature.books.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.BookCover
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

@Composable
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onReadClick: () -> Unit,
    onSaveClick: () -> Unit,
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
                    .verticalScroll(rememberScrollState())
                    .padding(FarhangiSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BookCover(
                    coverUrl = book.coverUrl,
                    title = book.title,
                    modifier = Modifier.width(FarhangiSize.coverDetailWidth),
                )
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = book.publisher.ifBlank { "ناشر ثبت نشده" },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${book.totalPages.toPersianDigits()} صفحه · دسته‌بندی: ${
                        book.categories.joinToString("، ").ifBlank { "—" }
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(book.description, style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onReadClick, modifier = Modifier.fillMaxWidth()) {
                    Text("مطالعه آنلاین")
                }
                Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth()) {
                    Text(if (book.isSaved) "حذف از کتاب‌خانه من" else "ذخیره در کتاب‌خانه من")
                }
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("بازگشت")
                }
            }
        }
    }
}
