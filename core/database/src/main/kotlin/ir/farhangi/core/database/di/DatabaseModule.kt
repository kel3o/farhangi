package ir.farhangi.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.farhangi.core.database.FarhangiDatabase
import ir.farhangi.core.database.dao.AudienceProfileDao
import ir.farhangi.core.database.dao.BookProgressDao
import ir.farhangi.core.database.dao.BookmarkDao
import ir.farhangi.core.database.dao.HighlightDao
import ir.farhangi.core.database.dao.OrgMessageDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FarhangiDatabase =
        Room.databaseBuilder(
            context,
            FarhangiDatabase::class.java,
            DATABASE_NAME,
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideBookProgressDao(database: FarhangiDatabase): BookProgressDao =
        database.bookProgressDao()

    @Provides
    fun provideBookmarkDao(database: FarhangiDatabase): BookmarkDao =
        database.bookmarkDao()

    @Provides
    fun provideHighlightDao(database: FarhangiDatabase): HighlightDao =
        database.highlightDao()

    @Provides
    fun provideAudienceProfileDao(database: FarhangiDatabase): AudienceProfileDao =
        database.audienceProfileDao()

    @Provides
    fun provideOrgMessageDao(database: FarhangiDatabase): OrgMessageDao =
        database.orgMessageDao()

    private const val DATABASE_NAME = "farhangi.db"
}
