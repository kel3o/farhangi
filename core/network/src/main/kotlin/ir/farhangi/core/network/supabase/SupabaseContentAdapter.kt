package ir.farhangi.core.network.supabase

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.gateway.ContentGateway
import ir.farhangi.core.network.model.AnnouncementDto
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.SearchResultDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase PostgREST content adapter.
 * Tables expected: books, courses, articles, announcements (see design plan).
 */
@Singleton
class SupabaseContentAdapter @Inject constructor(
    private val httpClient: SupabaseHttpClient,
) : ContentGateway {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getBooks(query: String?): Result<List<BookDto>> {
        return try {
            val path = buildListPath("books", query, "title")
            val body = httpClient.get(path)
            Result.Success(json.decodeFromString<List<BookDto>>(body))
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    override suspend fun getBook(id: String): Result<BookDto> =
        getById("books", id) { payload ->
            json.decodeFromString<List<BookDto>>(payload).firstOrNull()
        }

    override suspend fun getCourses(query: String?): Result<List<CourseDto>> {
        return try {
            val path = buildListPath("courses", query, "title")
            val body = httpClient.get(path)
            Result.Success(json.decodeFromString<List<CourseDto>>(body))
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    override suspend fun getCourse(id: String): Result<CourseDto> =
        getById("courses", id) { payload ->
            json.decodeFromString<List<CourseDto>>(payload).firstOrNull()
        }

    override suspend fun getArticles(query: String?): Result<List<ArticleDto>> {
        return try {
            val path = buildListPath("articles", query, "title")
            val body = httpClient.get(path)
            Result.Success(json.decodeFromString<List<ArticleDto>>(body))
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    override suspend fun getArticle(id: String): Result<ArticleDto> =
        getById("articles", id) { payload ->
            json.decodeFromString<List<ArticleDto>>(payload).firstOrNull()
        }

    override suspend fun getAnnouncements(): Result<List<AnnouncementDto>> {
        return try {
            val body = httpClient.get("/rest/v1/announcements?select=*&order=published_at.desc")
            Result.Success(json.decodeFromString<List<AnnouncementDto>>(body))
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    override suspend fun search(query: String, type: String?): Result<List<SearchResultDto>> {
        return try {
            if (query.isBlank()) {
                return Result.Success(emptyList())
            }
            val bookResults = mutableListOf<SearchResultDto>()
            val courseResults = mutableListOf<SearchResultDto>()
            val articleResults = mutableListOf<SearchResultDto>()

            if (type == null || type.equals("BOOK", ignoreCase = true)) {
                when (val books = getBooks(query)) {
                    is Result.Success -> books.data.forEach { book ->
                        bookResults += SearchResultDto(
                            id = book.id,
                            title = book.title,
                            subtitle = book.author,
                            type = "BOOK",
                            coverUrl = book.coverUrl,
                        )
                    }
                    else -> Unit
                }
            }
            if (type == null || type.equals("COURSE", ignoreCase = true)) {
                when (val courses = getCourses(query)) {
                    is Result.Success -> courses.data.forEach { course ->
                        courseResults += SearchResultDto(
                            id = course.id,
                            title = course.title,
                            subtitle = course.type,
                            type = "COURSE",
                            coverUrl = course.coverUrl,
                        )
                    }
                    else -> Unit
                }
            }
            if (type == null || (!type.equals("BOOK", true) && !type.equals("COURSE", true))) {
                when (val articles = getArticles(query)) {
                    is Result.Success -> articles.data
                        .filter { article -> type == null || article.type.equals(type, true) }
                        .forEach { article ->
                            articleResults += SearchResultDto(
                                id = article.id,
                                title = article.title,
                                subtitle = article.category,
                                type = article.type,
                                coverUrl = article.coverUrl,
                            )
                        }
                    else -> Unit
                }
            }
            Result.Success(bookResults + courseResults + articleResults)
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    private suspend fun <T> getById(
        table: String,
        id: String,
        parse: (String) -> T?,
    ): Result<T> {
        return try {
            val body = httpClient.get("/rest/v1/$table?id=eq.$id&select=*")
            val parsed = parse(body)
            if (parsed != null) {
                Result.Success(parsed)
            } else {
                Result.Error(NoSuchElementException("مورد یافت نشد"))
            }
        } catch (error: Throwable) {
            Result.Error(error)
        }
    }

    private fun buildListPath(table: String, query: String?, column: String): String {
        val base = "/rest/v1/$table?select=*"
        return if (query.isNullOrBlank()) {
            base
        } else {
            "$base&$column=ilike.*${query.trim()}*"
        }
    }
}
