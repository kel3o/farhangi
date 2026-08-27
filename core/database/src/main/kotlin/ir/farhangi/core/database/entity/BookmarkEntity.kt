package ir.farhangi.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "bookmarks",
    primaryKeys = ["user_id", "book_id", "page"],
)
data class BookmarkEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "book_id") val bookId: String,
    @ColumnInfo(name = "book_title") val bookTitle: String = "",
    @ColumnInfo(name = "page") val page: Int,
    @ColumnInfo(name = "note") val note: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
