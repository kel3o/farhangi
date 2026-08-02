package ir.farhangi.core.network.gateway

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.network.model.AnnouncementDto
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.SearchResultDto

/**
 * Backend-agnostic content contract for books, courses, magazine, and search.
 */
interface ContentGateway {
    suspend fun getBooks(query: String? = null): Result<List<BookDto>>
    suspend fun getBook(id: String): Result<BookDto>
    suspend fun getCourses(query: String? = null): Result<List<CourseDto>>
    suspend fun getCourse(id: String): Result<CourseDto>
    suspend fun getArticles(query: String? = null): Result<List<ArticleDto>>
    suspend fun getArticle(id: String): Result<ArticleDto>
    suspend fun getAnnouncements(): Result<List<AnnouncementDto>>
    suspend fun search(query: String, type: String? = null): Result<List<SearchResultDto>>
}
