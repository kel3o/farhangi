package ir.farhangi.core.data.usecase

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.data.repository.EngagementRepository
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.model.Announcement
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Contest
import ir.farhangi.core.model.ContestStatus
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.LeaderboardPeriod
import ir.farhangi.core.model.PointsBreakdown
import ir.farhangi.core.model.ScoreBoard
import ir.farhangi.core.model.Trophy
import ir.farhangi.core.network.gateway.ContentGateway
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import javax.inject.Inject

data class HomeFeed(
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
)

class GetHomeFeed @Inject constructor(
    private val bookRepository: BookRepository,
    private val courseRepository: CourseRepository,
    private val magazineRepository: MagazineRepository,
    private val engagementRepository: EngagementRepository,
    private val authRepository: AuthRepository,
    private val contentGateway: ContentGateway,
) {
    suspend operator fun invoke(): Result<HomeFeed> = coroutineScope {
        val booksDeferred = async { bookRepository.getBooks() }
        val coursesDeferred = async { courseRepository.getCourses() }
        val articlesDeferred = async { magazineRepository.getArticles() }
        val announcementsDeferred = async { contentGateway.getAnnouncements() }
        val contestsDeferred = async { engagementRepository.getContests() }
        val pointsDeferred = async { engagementRepository.getPoints() }
        val trophiesDeferred = async { engagementRepository.getTrophies() }
        val leaderboardDeferred = async {
            engagementRepository.getLeaderboard(LeaderboardPeriod.WEEKLY, ScoreBoard.OVERALL)
        }

        val booksResult = booksDeferred.await()
        val coursesResult = coursesDeferred.await()
        val articlesResult = articlesDeferred.await()
        val announcementsResult = announcementsDeferred.await()
        val contestsResult = contestsDeferred.await()
        val pointsResult = pointsDeferred.await()
        val trophiesResult = trophiesDeferred.await()
        val leaderboardResult = leaderboardDeferred.await()

        if (booksResult is Result.Error) return@coroutineScope booksResult
        if (coursesResult is Result.Error) return@coroutineScope coursesResult
        if (articlesResult is Result.Error) return@coroutineScope articlesResult
        if (announcementsResult is Result.Error) {
            return@coroutineScope Result.Error(announcementsResult.exception)
        }

        val books = (booksResult as Result.Success).data
        val courses = (coursesResult as Result.Success).data
        val articles = (articlesResult as Result.Success).data
        val announcements = (announcementsResult as Result.Success).data.map { dto ->
            Announcement(
                id = dto.id,
                title = dto.title,
                body = dto.body,
                publishedAt = Instant.parse(dto.publishedAt),
            )
        }
        val contests = (contestsResult as? Result.Success)?.data.orEmpty()
        val points = (pointsResult as? Result.Success)?.data ?: PointsBreakdown(0, 0, 0, 0)
        val trophies = (trophiesResult as? Result.Success)?.data.orEmpty()
        val weeklyRank = (leaderboardResult as? Result.Success)?.data
            ?.firstOrNull { it.isCurrentUser }
            ?.rank

        val session = authRepository.observeSession().first()
        val progressList = if (session != null) {
            bookRepository.observeContinueReading(session.userId).first()
        } else {
            emptyList()
        }
        val booksById = books.associateBy { it.id }
        val continueReading = progressList.mapNotNull { booksById[it.bookId] }
            .ifEmpty { books.take(CONTINUE_FALLBACK_COUNT) }

        Result.Success(
            HomeFeed(
                continueReading = continueReading,
                latestArticles = articles.take(LATEST_ARTICLES_COUNT),
                recommendedBooks = books.take(RECOMMENDED_COUNT),
                recentlyAdded = books.take(RECENTLY_ADDED_COUNT),
                announcements = announcements,
                continueCourses = courses.filter { it.progress > 0f }.ifEmpty { courses.take(1) },
                liveContests = contests.filter { it.status == ContestStatus.LIVE }.take(LIVE_CONTEST_COUNT),
                points = points,
                weeklyRank = weeklyRank,
                readingMinutesThisWeek = points.reading,
                trophies = trophies,
            ),
        )
    }

    companion object {
        private const val RECENTLY_ADDED_COUNT = 4
        private const val RECOMMENDED_COUNT = 6
        private const val LATEST_ARTICLES_COUNT = 4
        private const val LIVE_CONTEST_COUNT = 3
        private const val CONTINUE_FALLBACK_COUNT = 3
    }
}
