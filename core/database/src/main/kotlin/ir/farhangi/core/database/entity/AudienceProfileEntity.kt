package ir.farhangi.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audience_profiles")
data class AudienceProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "age") val age: Int,
)
