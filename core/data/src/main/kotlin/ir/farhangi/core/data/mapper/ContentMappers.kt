package ir.farhangi.core.data.mapper

import ir.farhangi.core.model.Article
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Course
import ir.farhangi.core.model.CourseSection
import ir.farhangi.core.model.CourseType
import ir.farhangi.core.model.MediaType
import ir.farhangi.core.model.SearchContentType
import ir.farhangi.core.model.SearchResult
import ir.farhangi.core.network.model.ArticleDto
import ir.farhangi.core.network.model.BookDto
import ir.farhangi.core.network.model.CourseDto
import ir.farhangi.core.network.model.SearchResultDto
import kotlinx.datetime.Instant

fun BookDto.toDomain(isSaved: Boolean = false): Book = Book(
    id = id,
    title = title,
    author = author,
    publisher = publisher,
    coverUrl = coverUrl,
    categories = categories,
    totalPages = totalPages,
    rating = rating,
    description = description,
    pdfUrl = pdfUrl,
    pages = pages,
    isSaved = isSaved,
)

fun CourseDto.toDomain(): Course = Course(
    id = id,
    title = title,
    type = runCatching { CourseType.valueOf(type) }.getOrDefault(CourseType.PRACTICAL),
    instructor = instructor,
    coverUrl = coverUrl,
    description = description,
    category = category,
    level = level.ifBlank { Course.LEVEL_BEGINNER },
    isFree = isFree,
    sections = sections.map {
        CourseSection(
            id = it.id,
            title = it.title,
            order = it.order,
            durationMinutes = it.durationMinutes,
            isCompleted = it.isCompleted,
            contentType = runCatching { ir.farhangi.core.model.LessonContentType.valueOf(it.contentType) }
                .getOrDefault(ir.farhangi.core.model.LessonContentType.ARTICLE),
            aparatUrl = it.aparatUrl,
            body = it.body,
        )
    },
    progress = progress,
)

fun ArticleDto.toDomain(isSaved: Boolean = false): Article = Article(
    id = id,
    title = title,
    type = runCatching { MediaType.valueOf(type) }.getOrDefault(MediaType.TEXT),
    category = runCatching { ir.farhangi.core.model.MagazineCategory.valueOf(category) }
        .getOrDefault(ir.farhangi.core.model.MagazineCategory.CULTURE),
    summary = summary,
    body = body,
    mediaUrl = mediaUrl,
    coverUrl = coverUrl,
    publishedAt = Instant.parse(publishedAt),
    isSaved = isSaved,
)

fun SearchResultDto.toDomain(): SearchResult = SearchResult(
    id = id,
    title = title,
    subtitle = subtitle,
    type = when (type.uppercase()) {
        "BOOK" -> SearchContentType.BOOK
        "COURSE" -> SearchContentType.COURSE
        "VIDEO" -> SearchContentType.VIDEO
        "AUDIO", "PODCAST" -> SearchContentType.AUDIO
        else -> SearchContentType.ARTICLE
    },
    coverUrl = coverUrl,
)