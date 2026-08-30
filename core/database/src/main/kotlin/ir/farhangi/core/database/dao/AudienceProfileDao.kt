package ir.farhangi.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ir.farhangi.core.database.entity.AudienceProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudienceProfileDao {
    @Query("SELECT * FROM audience_profiles WHERE user_id = :userId LIMIT 1")
    fun observe(userId: String): Flow<AudienceProfileEntity?>

    @Upsert
    suspend fun upsert(profile: AudienceProfileEntity)
}
