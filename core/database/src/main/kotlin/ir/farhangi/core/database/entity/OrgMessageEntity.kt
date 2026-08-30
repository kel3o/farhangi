package ir.farhangi.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "org_messages")
data class OrgMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "from_name") val fromName: String,
    @ColumnInfo(name = "from_role") val fromRole: String,
    val title: String,
    val body: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "is_read") val isRead: Boolean,
    val recipient: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
)
