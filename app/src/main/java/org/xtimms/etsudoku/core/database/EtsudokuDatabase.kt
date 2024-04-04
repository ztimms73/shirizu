package org.xtimms.etsudoku.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.xtimms.etsudoku.core.database.dao.BookmarksDao
import org.xtimms.etsudoku.core.database.dao.FavouriteCategoriesDao
import org.xtimms.etsudoku.core.database.dao.FavouritesDao
import org.xtimms.etsudoku.core.database.dao.HistoryDao
import org.xtimms.etsudoku.core.database.dao.MangaDao
import org.xtimms.etsudoku.core.database.dao.MangaSourcesDao
import org.xtimms.etsudoku.core.database.dao.SuggestionDao
import org.xtimms.etsudoku.core.database.dao.TagsDao
import org.xtimms.etsudoku.core.database.dao.TrackLogsDao
import org.xtimms.etsudoku.core.database.dao.TracksDao
import org.xtimms.etsudoku.core.database.entity.BookmarkEntity
import org.xtimms.etsudoku.core.database.entity.FavouriteCategoryEntity
import org.xtimms.etsudoku.core.database.entity.FavouriteEntity
import org.xtimms.etsudoku.core.database.entity.HistoryEntity
import org.xtimms.etsudoku.core.database.entity.MangaEntity
import org.xtimms.etsudoku.core.database.entity.MangaSourceEntity
import org.xtimms.etsudoku.core.database.entity.MangaTagsEntity
import org.xtimms.etsudoku.core.database.entity.SuggestionEntity
import org.xtimms.etsudoku.core.database.entity.TagEntity
import org.xtimms.etsudoku.core.database.entity.TrackEntity
import org.xtimms.etsudoku.core.database.entity.TrackLogEntity
import org.xtimms.etsudoku.utils.lang.processLifecycleScope

const val DATABASE_VERSION = 1

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
        TrackLogEntity::class
    ],
    version = DATABASE_VERSION
)
abstract class EtsudokuDatabase : RoomDatabase() {

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

}

fun EtsudokuDatabase(context: Context): EtsudokuDatabase = Room
    .databaseBuilder(context, EtsudokuDatabase::class.java, "etsudoku-db")
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