package ir.farhangi.core.data.usecase

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.model.Announcement
import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.MediaType
import ir.farhangi.core.network.gateway.ContentGateway
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import javax.inject.Inject

data class HomeFeed(
    val continueReading: List<Book>,
    val continueWatching: List<Article>,
    val latestArticles: List<Article>,
    val recommendedBooks: List<Book>,
    val recentlyAdded: List<Book>,
    val announcements: List<Announcement>,
    val continueCourses: List<Course>,
    val dailyQuote: String,
)

class GetHomeFeed @Inject constructor(
    private val bookRepository: BookRepository,
    private val courseRepository: CourseRepository,
    private val magazineRepository: MagazineRepository,
    private val authRepository: AuthRepository,
    private val contentGateway: ContentGateway,
) {
    suspend operator fun invoke(): Result<HomeFeed> = coroutineScope {
        val booksDeferred = async { bookRepository.getBooks() }
        val coursesDeferred = async { courseRepository.getCourses() }
        val articlesDeferred = async { magazineRepository.getArticles() }
        val announcementsDeferred = async { contentGateway.getAnnouncements() }

        val booksResult = booksDeferred.await()
        val coursesResult = coursesDeferred.await()
        val articlesResult = articlesDeferred.await()
        val announcementsResult = announcementsDeferred.await()

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

        val session = authRepository.observeSession().first()
        val progressList = if (session != null) {
            bookRepository.observeContinueReading(session.userId).first()
        } else {
            emptyList()
        }
        val booksById = books.associateBy { it.id }
        val continueReading = progressList.mapNotNull { booksById[it.bookId] }

        Result.Success(
            HomeFeed(
                continueReading = continueReading,
                continueWatching = articles.filter {
                    it.type == MediaType.VIDEO || it.type == MediaType.AUDIO || it.type == MediaType.PODCAST
                },
                latestArticles = articles,
                recommendedBooks = books,
                recentlyAdded = books.takeLast(RECENTLY_ADDED_COUNT).reversed(),
                announcements = announcements,
                continueCourses = courses.filter { it.progress > 0f },
                dailyQuote = DAILY_QUOTE,
            ),
        )
    }

    companion object {
        private const val DAILY_QUOTE = "«توانا بود هر که دانا بود» — فردوسی"
        private const val RECENTLY_ADDED_COUNT = 3
    }
}
