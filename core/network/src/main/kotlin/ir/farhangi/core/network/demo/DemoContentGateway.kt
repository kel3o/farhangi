package ir.farhangi.core.network.demo

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.gateway.ContentGateway
import ir.farhangi.core.network.model.AnnouncementDto
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.SearchResultDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoContentGateway @Inject constructor(
    private val store: DemoPlatformStore,
) : ContentGateway {

    override suspend fun getBooks(query: String?): Result<List<BookDto>> {
        delay(NETWORK_DELAY_MS)
        val filtered = store.books.value.filterByQuery(query) { it.title + it.author }
        return Result.Success(filtered)
    }

    override suspend fun getBook(id: String): Result<BookDto> {
        delay(NETWORK_DELAY_MS)
        return store.books.value.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("کتاب یافت نشد"))
    }

    override suspend fun getCourses(query: String?): Result<List<CourseDto>> {
        delay(NETWORK_DELAY_MS)
        val filtered = store.courses.value.filterByQuery(query) { it.title }
        return Result.Success(filtered)
    }

    override suspend fun getCourse(id: String): Result<CourseDto> {
        delay(NETWORK_DELAY_MS)
        return store.courses.value.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("دوره یافت نشد"))
    }

    override suspend fun getArticles(query: String?): Result<List<ArticleDto>> {
        delay(NETWORK_DELAY_MS)
        val filtered = store.articles.value
            .filterByQuery(query) { it.title + it.category }
            .sortedByDescending { it.publishedAt }
        return Result.Success(filtered)
    }

    override suspend fun getArticle(id: String): Result<ArticleDto> {
        delay(NETWORK_DELAY_MS)
        return store.articles.value.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(NoSuchElementException("مقاله یافت نشد"))
    }

    override suspend fun getAnnouncements(): Result<List<AnnouncementDto>> {
        delay(NETWORK_DELAY_MS)
        return Result.Success(store.announcements.value)
    }

    override suspend fun search(query: String, type: String?): Result<List<SearchResultDto>> {
        delay(NETWORK_DELAY_MS)
        if (query.isBlank()) return Result.Success(emptyList())
        val q = query.trim()
        val bookResults = store.books.value
            .filter { it.title.contains(q, ignoreCase = true) || it.author.contains(q, ignoreCase = true) }
            .map { SearchResultDto(it.id, it.title, it.author, "BOOK", it.coverUrl) }
        val courseResults = store.courses.value
            .filter { it.title.contains(q, ignoreCase = true) }
            .map { SearchResultDto(it.id, it.title, it.type, "COURSE", it.coverUrl) }
        val articleResults = store.articles.value
            .filter { it.title.contains(q, ignoreCase = true) }
            .map { SearchResultDto(it.id, it.title, it.category, it.type, it.coverUrl) }
        val all = (bookResults + courseResults + articleResults).filter {
            type == null || it.type.equals(type, ignoreCase = true)
        }
        return Result.Success(all)
    }

    private fun <T> List<T>.filterByQuery(query: String?, text: (T) -> String): List<T> {
        if (query.isNullOrBlank()) return this
        return filter { text(it).contains(query.trim(), ignoreCase = true) }
    }

    companion object {
        private const val NETWORK_DELAY_MS = 200L
    }
}
