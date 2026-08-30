package ir.farhangi.feature.home.impl

import ir.farhangi.core.model.Announcement
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.PointsBreakdown
import ir.farhangi.core.model.Trophy

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val continueReading: List<Book>,
        val latestArticles: List<Article>,
        val recommendedBooks: List<Book>,
        val recentlyAdded: List<Book>,
        val announcements: List<Announcement>,
        val continueCourses: List<Course>,
        val liveContests: List<Contest>,
        val points: PointsBreakdown,
        val weeklyRank: Int?,
        val readingMinutesThisWeek: Int,
        val trophies: List<Trophy>,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
