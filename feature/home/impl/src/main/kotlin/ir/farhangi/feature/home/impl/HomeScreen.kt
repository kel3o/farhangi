package ir.farhangi.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ir.farhangi.core.designsystem.theme.FarhangiSpacing
import ir.farhangi.core.model.Announcement
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.Course
import ir.farhangi.core.ui.ArticleCard
import ir.farhangi.core.ui.BookCard
import ir.farhangi.core.ui.ContestCard
import ir.farhangi.core.ui.CourseCard
import ir.farhangi.core.ui.EmptyState
import ir.farhangi.core.ui.LoadingState
import ir.farhangi.core.ui.PointsSummaryCard
import ir.farhangi.core.ui.SectionHeader

private val HomeBlurRadius = 6.dp
private const val HOME_DIM_ALPHA = 0.20f

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onBookClick: (Book) -> Unit,
    onCourseClick: (Course) -> Unit,
    onArticleClick: (Article) -> Unit,
    onContestClick: (Contest) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    var showIncompleteDialog by remember { mutableStateOf(true) }
    val interactionsEnabled = false

    Box(modifier = modifier.fillMaxSize()) {
        HomeContent(
            uiState = uiState,
            onBookClick = if (interactionsEnabled) onBookClick else { {} },
            onCourseClick = if (interactionsEnabled) onCourseClick else { {} },
            onArticleClick = if (interactionsEnabled) onArticleClick else { {} },
            onContestClick = if (interactionsEnabled) onContestClick else { {} },
            interactionsEnabled = interactionsEnabled,
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize()
                .blur(HomeBlurRadius)
                .graphicsLayer { alpha = 1f - HOME_DIM_ALPHA },
        )
        if (showIncompleteDialog) {
            AlertDialog(
                onDismissRequest = { /* باید متوجه شدم زده شود */ },
                title = { Text("خانه") },
                text = { Text("این بخش در حال تکمیل است") },
                confirmButton = {
                    TextButton(onClick = { showIncompleteDialog = false }) {
                        Text("متوجه شدم")
                    }
                },
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onBookClick: (Book) -> Unit,
    onCourseClick: (Course) -> Unit,
    onArticleClick: (Article) -> Unit,
    onContestClick: (Contest) -> Unit,
    interactionsEnabled: Boolean,
    contentPadding: PaddingValues,
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
                userScrollEnabled = true,
            ) {
                item {
                    PointsSummaryCard(
                        points = uiState.points,
                        weeklyRank = uiState.weeklyRank,
                        readingMinutes = uiState.readingMinutesThisWeek,
                        modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                    )
                }
                if (uiState.trophies.isNotEmpty()) {
                    item { SectionHeader(title = "جام‌های شما") }
                    item {
                        Text(
                            text = uiState.trophies.joinToString(" · ") { it.title },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        )
                    }
                }
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = uiState.dailyQuote,
                            modifier = Modifier.padding(FarhangiSpacing.md),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
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
                        BookRow(
                            books = uiState.continueReading,
                            onBookClick = onBookClick,
                            interactionsEnabled = interactionsEnabled,
                        )
                    }
                }
                if (uiState.continueCourses.isNotEmpty()) {
                    item { SectionHeader(title = "ادامه یادگیری") }
                    items(uiState.continueCourses, key = { it.id }) { course ->
                        CourseCard(
                            course = course,
                            onClick = { if (interactionsEnabled) onCourseClick(course) },
                            modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        )
                    }
                }
                if (uiState.liveContests.isNotEmpty()) {
                    item { SectionHeader(title = "مسابقه‌های در جریان") }
                    items(uiState.liveContests, key = { it.id }) { contest ->
                        ContestCard(
                            contest = contest,
                            onClick = { if (interactionsEnabled) onContestClick(contest) },
                            modifier = Modifier.padding(horizontal = FarhangiSpacing.md),
                        )
                    }
                }
                item { SectionHeader(title = "پیشنهاد کتاب") }
                item {
                    BookRow(
                        books = uiState.recommendedBooks,
                        onBookClick = onBookClick,
                        interactionsEnabled = interactionsEnabled,
                    )
                }
                item { SectionHeader(title = "تازه‌های مجله") }
                items(uiState.latestArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { if (interactionsEnabled) onArticleClick(article) },
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
    interactionsEnabled: Boolean,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = FarhangiSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(FarhangiSpacing.sm),
        userScrollEnabled = true,
    ) {
        items(books, key = { it.id }) { book ->
            BookCard(
                book = book,
                onClick = { if (interactionsEnabled) onBookClick(book) },
            )
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
