package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.common.result.map
import ir.farhangi.core.data.mapper.toDomain
import ir.farhangi.core.database.dao.BookProgressDao
import ir.farhangi.core.database.dao.BookmarkDao
import ir.farhangi.core.database.dao.HighlightDao
import ir.farhangi.core.database.entity.BookProgressEntity
import ir.farhangi.core.database.entity.BookmarkEntity
import ir.farhangi.core.database.entity.HighlightEntity
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Bookmark
import ir.farhangi.core.model.Highlight
import ir.farhangi.core.model.ReadingProgress
import ir.farhangi.core.network.gateway.ContentGateway
import ir.farhangi.core.network.gateway.EngagementGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBookRepository @Inject constructor(
    private val contentGateway: ContentGateway,
    private val engagementGateway: EngagementGateway,
    private val bookProgressDao: BookProgressDao,
    private val bookmarkDao: BookmarkDao,
    private val highlightDao: HighlightDao,
) : BookRepository {

    override suspend fun getBooks(query: String?): Result<List<Book>> {
        val saved = savedIds()
        return contentGateway.getBooks(query).map { list ->
            list.map { it.toDomain(isSaved = it.id in saved) }
        }
    }

    override suspend fun getBook(id: String): Result<Book> {
        val saved = savedIds()
        return contentGateway.getBook(id).map { it.toDomain(isSaved = it.id in saved) }
    }

    private suspend fun savedIds(): Set<String> = when (val result = engagementGateway.getSavedBookIds()) {
        is Result.Success -> result.data
        else -> emptySet()
    }

    override fun observeProgress(userId: String, bookId: String): Flow<ReadingProgress?> =
        bookProgressDao.observeProgress(userId, bookId).map { it?.toDomain() }

    override fun observeContinueReading(userId: String): Flow<List<ReadingProgress>> =
        bookProgressDao.observeAllForUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun updateProgress(
        userId: String,
        bookId: String,
        page: Int,
        totalPages: Int,
    ) {
        val safeTotal = totalPages.coerceAtLeast(1)
        val safePage = page.coerceIn(1, safeTotal)
        val percent = safePage.toFloat() / safeTotal.toFloat()
        bookProgressDao.upsert(
            BookProgressEntity(
                userId = userId,
                bookId = bookId,
                page = safePage,
                percent = percent,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    override fun observeBookmark(userId: String, bookId: String, page: Int): Flow<Bookmark?> =
        bookmarkDao.observeBookmark(userId, bookId, page).map { it?.toDomain() }

    override suspend fun toggleBookmark(
        userId: String,
        bookId: String,
        page: Int,
        note: String,
    ) {
        val existing = bookmarkDao.observeBookmark(userId, bookId, page).first()
        if (existing != null) {
            bookmarkDao.delete(userId, bookId, page)
        } else {
            bookmarkDao.upsert(
                BookmarkEntity(
                    userId = userId,
                    bookId = bookId,
                    page = page,
                    note = note,
                    createdAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    override fun observeHighlights(
        userId: String,
        bookId: String,
        page: Int,
    ): Flow<List<Highlight>> =
        highlightDao.observeForPage(userId, bookId, page).map { list -> list.map { it.toDomain() } }

    override suspend fun addHighlight(
        userId: String,
        bookId: String,
        page: Int,
        text: String,
    ): Highlight {
        val entity = HighlightEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            bookId = bookId,
            page = page,
            text = text,
            createdAt = System.currentTimeMillis(),
        )
        highlightDao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun removeHighlight(highlightId: String) {
        highlightDao.deleteById(highlightId)
    }

    private fun BookProgressEntity.toDomain() = ReadingProgress(
        bookId = bookId,
        page = page,
        percent = percent,
        updatedAtEpochMs = updatedAt,
    )

    private fun BookmarkEntity.toDomain() = Bookmark(
        bookId = bookId,
        page = page,
        note = note,
        createdAtEpochMs = createdAt,
    )

    private fun HighlightEntity.toDomain() = Highlight(
        id = id,
        bookId = bookId,
        page = page,
        text = text,
        createdAtEpochMs = createdAt,
    )
}
