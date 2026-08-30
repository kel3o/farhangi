package ir.farhangi.feature.books.impl

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiActionColors
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.DEFAULT_BOOK_PURCHASE_URL
import ir.farhangi.core.model.toPersianDigits
import ir.farhangi.core.ui.BookCover
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState

private const val DESCRIPTION_COLLAPSED_LINES = 4

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
            val context = LocalContext.current
            var descriptionExpanded by remember(book.id) { mutableStateOf(false) }
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
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Text(
                    text = "${book.totalPages.toPersianDigits()} صفحه · دسته‌بندی: ${
                        book.categories.joinToString("، ").ifBlank { "—" }
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                ExpandableDescription(
                    text = book.description,
                    expanded = descriptionExpanded,
                    onToggle = { descriptionExpanded = !descriptionExpanded },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = onReadClick, modifier = Modifier.fillMaxWidth()) {
                    Text("مطالعه خلاصه کتاب")
                }
                Button(
                    onClick = {
                        val url = book.purchaseUrl.ifBlank { DEFAULT_BOOK_PURCHASE_URL }
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarhangiActionColors.Purchase,
                        contentColor = FarhangiActionColors.OnPurchase,
                    ),
                ) {
                    Text("تهیه کتاب از طاقچه")
                }
                Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth()) {
                    Text(if (book.isSaved) "حذف از کتابخانه من" else "ذخیره در کتابخانه من")
                }
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("بازگشت")
                }
            }
        }
    }
}

@Composable
private fun ExpandableDescription(
    text: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs)) {
        Box {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (expanded) Int.MAX_VALUE else DESCRIPTION_COLLAPSED_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            if (!expanded) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.55f to Color.Transparent,
                                    1f to MaterialTheme.colorScheme.surface,
                                ),
                            ),
                        ),
                )
            }
        }
        TextButton(
            onClick = onToggle,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(if (expanded) "کمتر" else "بیشتر")
        }
    }
}
