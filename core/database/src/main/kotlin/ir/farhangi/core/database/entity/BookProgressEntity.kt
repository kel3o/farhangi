package ir.farhangi.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "book_progress",
    primaryKeys = ["user_id", "book_id"],
)
data class BookProgressEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "book_id") val bookId: String,
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "percent") val percent: Float,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)