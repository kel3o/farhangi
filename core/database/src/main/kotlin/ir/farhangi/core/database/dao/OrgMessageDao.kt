package ir.farhangi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.farhangi.core.database.entity.OrgMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrgMessageDao {
    @Query("SELECT * FROM org_messages ORDER BY created_at DESC")
    suspend fun getAll(): List<OrgMessageEntity>

    @Query("SELECT * FROM org_messages WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): OrgMessageEntity?

    @Query("SELECT * FROM org_messages WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<OrgMessageEntity?>

    @Upsert
    suspend fun upsert(message: OrgMessageEntity)

    @Upsert
    suspend fun upsertAll(messages: List<OrgMessageEntity>)

    @Query("UPDATE org_messages SET is_read = 1 WHERE id = :id")
    suspend fun markRead(id: String)
}
