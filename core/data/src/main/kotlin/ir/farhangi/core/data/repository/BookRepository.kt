package ir.farhangi.core.data.repository

import ir.farhangi.core.common.result.Result
import ir.farhangi.core.model.Book
import ir.farhangi.core.model.Bookmark
import ir.farhangi.core.model.Highlight
import ir.farhangi.core.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks(query: String? = null): Result<List<Book>>
    suspend fun getBook(id: String): Result<Book>

    fun observeProgress(userId: String, bookId: String): Flow<ReadingProgress?>
    fun observeContinueReading(userId: String): Flow<List<ReadingProgress>>
    suspend fun updateProgress(userId: String, bookId: String, page: Int, totalPages: Int)

    fun observeBookmark(userId: String, bookId: String, page: Int): Flow<Bookmark?>
    suspend fun toggleBookmark(userId: String, bookId: String, page: Int, note: String = "")

    fun observeHighlights(userId: String, bookId: String, page: Int): Flow<List<Highlight>>
    suspend fun addHighlight(userId: String, bookId: String, page: Int, text: String): Highlight
    suspend fun removeHighlight(highlightId: String)
}
