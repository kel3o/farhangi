package ir.farhangi.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.farhangi.core.database.dao.BookProgressDao
import ir.farhangi.core.database.dao.BookmarkDao
import ir.farhangi.core.database.dao.HighlightDao
import ir.farhangi.core.database.entity.BookProgressEntity
import ir.farhangi.core.database.entity.BookmarkEntity
import ir.farhangi.core.database.entity.HighlightEntity

@Database(
    entities = [
        BookProgressEntity::class,
        BookmarkEntity::class,
        HighlightEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class FarhangiDatabase : RoomDatabase() {
    abstract fun bookProgressDao(): BookProgressDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun highlightDao(): HighlightDao
}
