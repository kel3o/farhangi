package ir.farhangi.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import ir.farhangi.core.designsystem.theme.FarhangiSize
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Book

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
) {
    Column(
        modifier = modifier
            .then(if (compact) Modifier.width(FarhangiSize.coverWidth) else Modifier.fillMaxWidth())
            .clickable(role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.xxs),
    ) {
        BookCover(
            coverUrl = book.coverUrl,
            title = book.title,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = book.title,
            style = if (compact) {
                MaterialTheme.typography.titleSmall
            } else {
                MaterialTheme.typography.bodySmall
            },
            maxLines = if (compact) 2 else 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = book.author,
            style = if (compact) {
                MaterialTheme.typography.bodySmall
            } else {
                MaterialTheme.typography.labelSmall
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
