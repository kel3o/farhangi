package ir.farhangi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.farhangi.core.database.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query(
        "SELECT * FROM bookmarks WHERE user_id = :userId AND book_id = :bookId AND page = :page LIMIT 1",
    )
    fun observeBookmark(userId: String, bookId: String, page: Int): Flow<BookmarkEntity?>

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId AND book_id = :bookId")
    fun observeBookmarks(userId: String, bookId: String): Flow<List<BookmarkEntity>>

    @Upsert
    suspend fun upsert(bookmark: BookmarkEntity)

    @Query(
        "DELETE FROM bookmarks WHERE user_id = :userId AND book_id = :bookId AND page = :page",
    )
    suspend fun delete(userId: String, bookId: String, page: Int)
}
