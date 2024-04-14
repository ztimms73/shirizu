package org.xtimms.shirizu.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.xtimms.shirizu.core.database.dao.BookmarksDao
import org.xtimms.shirizu.core.database.dao.FavouriteCategoriesDao
import org.xtimms.shirizu.core.database.dao.FavouritesDao
import org.xtimms.shirizu.core.database.dao.HistoryDao
import org.xtimms.shirizu.core.database.dao.MangaDao
import org.xtimms.shirizu.core.database.dao.MangaSourcesDao
import org.xtimms.shirizu.core.database.dao.StatsDao
import org.xtimms.shirizu.core.database.dao.SuggestionDao
import org.xtimms.shirizu.core.database.dao.TagsDao
import org.xtimms.shirizu.core.database.dao.TrackLogsDao
import org.xtimms.shirizu.core.database.dao.TracksDao
import org.xtimms.shirizu.core.database.entity.BookmarkEntity
import org.xtimms.shirizu.core.database.entity.FavouriteCategoryEntity
import org.xtimms.shirizu.core.database.entity.FavouriteEntity
import org.xtimms.shirizu.core.database.entity.HistoryEntity
import org.xtimms.shirizu.core.database.entity.MangaEntity
import org.xtimms.shirizu.core.database.entity.MangaSourceEntity
import org.xtimms.shirizu.core.database.entity.MangaTagsEntity
import org.xtimms.shirizu.core.database.entity.StatsEntity
import org.xtimms.shirizu.core.database.entity.SuggestionEntity
import org.xtimms.shirizu.core.database.entity.TagEntity
import org.xtimms.shirizu.core.database.entity.TrackEntity
import org.xtimms.shirizu.core.database.entity.TrackLogEntity
import org.xtimms.shirizu.core.database.migrations.Migration1To2
import org.xtimms.shirizu.core.database.migrations.Migration2To3
import org.xtimms.shirizu.utils.lang.processLifecycleScope

const val DATABASE_VERSION = 3

@Database(
    entities = [
        MangaEntity::class,
        TagEntity::class,
        MangaTagsEntity::class,
        MangaSourceEntity::class,
        HistoryEntity::class,
        FavouriteEntity::class,
        FavouriteCategoryEntity::class,
        BookmarkEntity::class,
        SuggestionEntity::class,
        TrackEntity::class,
        TrackLogEntity::class,
        StatsEntity::class,
    ],
    version = DATABASE_VERSION
)
abstract class ShirizuDatabase : RoomDatabase() {

    abstract fun getTagsDao(): TagsDao

    abstract fun getHistoryDao(): HistoryDao

    abstract fun getMangaDao(): MangaDao

    abstract fun getSourcesDao(): MangaSourcesDao

    abstract fun getFavouritesDao(): FavouritesDao

    abstract fun getFavouriteCategoriesDao(): FavouriteCategoriesDao

    abstract fun getBookmarksDao(): BookmarksDao

    abstract fun getSuggestionDao(): SuggestionDao

    abstract fun getTracksDao(): TracksDao

    abstract fun getTrackLogsDao(): TrackLogsDao

    abstract fun getStatsDao(): StatsDao

}

fun getDatabaseMigrations(context: Context): Array<Migration> = arrayOf(
    Migration1To2(),
    Migration2To3()
)

fun ShirizuDatabase(context: Context): ShirizuDatabase = Room
    .databaseBuilder(context, ShirizuDatabase::class.java, "shirizu-db")
    .addMigrations(*getDatabaseMigrations(context))
    .addCallback(DatabasePrePopulateCallback(context.resources))
    .build()

@OptIn(ExperimentalCoroutinesApi::class)
fun InvalidationTracker.removeObserverAsync(observer: InvalidationTracker.Observer) {
    val scope = processLifecycleScope
    if (scope.isActive) {
        processLifecycleScope.launch(Dispatchers.Default, CoroutineStart.ATOMIC) {
            removeObserver(observer)
        }
    }
}