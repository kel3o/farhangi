package ir.farhangi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.farhangi.core.database.entity.BookProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookProgressDao {
    @Query("SELECT * FROM book_progress WHERE user_id = :userId AND book_id = :bookId LIMIT 1")
    fun observeProgress(userId: String, bookId: String): Flow<BookProgressEntity?>

    @Query("SELECT * FROM book_progress WHERE user_id = :userId ORDER BY updated_at DESC")
    fun observeAllForUser(userId: String): Flow<List<BookProgressEntity>>

    @Query("SELECT * FROM book_progress WHERE user_id = :userId ORDER BY updated_at DESC")
    suspend fun getAllForUser(userId: String): List<BookProgressEntity>

    @Upsert
    suspend fun upsert(progress: BookProgressEntity)
}
