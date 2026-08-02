package ir.farhangi.feature.home.impl

import ir.farhangi.core.model.Announcement
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Course

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val continueReading: List<Book>,
        val continueWatching: List<Article>,
        val latestArticles: List<Article>,
        val recommendedBooks: List<Book>,
        val recentlyAdded: List<Book>,
        val announcements: List<Announcement>,
        val continueCourses: List<Course>,
        val dailyQuote: String,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
