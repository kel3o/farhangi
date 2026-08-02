package ir.farhangi.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Announcement
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Course
import ir.farhangi.core.ui.ArticleCard
import ir.farhangi.core.ui.BookCard
import ir.farhangi.core.ui.CourseCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.SectionHeader

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onBookClick: (Book) -> Unit,
    onCourseClick: (Course) -> Unit,
    onArticleClick: (Article) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        HomeUiState.Loading -> LoadingState(modifier = modifier.padding(contentPadding))
        is HomeUiState.Error -> EmptyState(
            title = "خطا",
            message = uiState.message,
            modifier = modifier.padding(contentPadding),
        )
        is HomeUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding() + FarhangiSpacing.sm,
                    bottom = contentPadding.calculateBottomPadding() + FarhangiSpacing.lg,
                ),
                verticalArrangement = Arrangement.spacedBy(FarhangiSpacing.md),
            ) {
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = uiState.dailyQuote,
                            modifier = Modifier.padding(FarhangiSpacing.md),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                if (uiState.announcements.isNotEmpty()) {
                    item { SectionHeader(title = "اطلاعیه‌ها") }
                    items(uiState.announcements, key = { it.id }) { announcement ->
                        AnnouncementRow(
                            announcement = announcement,
                            modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        )
                    }
                }
                if (uiState.continueReading.isNotEmpty()) {
                    item { SectionHeader(title = "ادامه مطالعه") }
                    item {
                        BookRow(books = uiState.continueReading, onBookClick = onBookClick)
                    }
                }
                if (uiState.continueWatching.isNotEmpty()) {
                    item { SectionHeader(title = "ادامه تماشا") }
                    items(uiState.continueWatching, key = { it.id }) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) },
                            modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        )
                    }
                }
                if (uiState.continueCourses.isNotEmpty()) {
                    item { SectionHeader(title = "ادامه یادگیری") }
                    items(uiState.continueCourses, key = { it.id }) { course ->
                        CourseCard(
                            course = course,
                            onClick = { onCourseClick(course) },
                            modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        )
                    }
                }
                item { SectionHeader(title = "پیشنهاد کتاب") }
                item {
                    BookRow(books = uiState.recommendedBooks, onBookClick = onBookClick)
                }
                item { SectionHeader(title = "تازه‌ها") }
                item {
                    BookRow(books = uiState.recentlyAdded, onBookClick = onBookClick)
                }
                item { SectionHeader(title = "تازه‌های مجله") }
                items(uiState.latestArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { onArticleClick(article) },
                        modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                    )
                }
            }
        }
    }
}

@Composable
private fun BookRow(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(book = book, onClick = { onBookClick(book) })
        }
    }
}

@Composable
private fun AnnouncementRow(
    announcement: Announcement,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(modifier = Modifier.padding(FarhangiSpacing.md)) {
            Text(announcement.title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
