package org.xtimms.tokusho.core.database

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
import org.xtimms.tokusho.core.database.dao.BookmarksDao
import org.xtimms.tokusho.core.database.dao.FavouriteCategoriesDao
import org.xtimms.tokusho.core.database.dao.FavouritesDao
import org.xtimms.tokusho.core.database.dao.HistoryDao
import org.xtimms.tokusho.core.database.dao.MangaDao
import org.xtimms.tokusho.core.database.dao.MangaSourcesDao
import org.xtimms.tokusho.core.database.dao.TagsDao
import org.xtimms.tokusho.core.database.entity.BookmarkEntity
import org.xtimms.tokusho.core.database.entity.FavouriteCategoryEntity
import org.xtimms.tokusho.core.database.entity.FavouriteEntity
import org.xtimms.tokusho.core.database.entity.HistoryEntity
import org.xtimms.tokusho.core.database.entity.MangaEntity
import org.xtimms.tokusho.core.database.entity.MangaSourceEntity
import org.xtimms.tokusho.core.database.entity.MangaTagsEntity
import org.xtimms.tokusho.core.database.entity.TagEntity
import org.xtimms.tokusho.utils.lang.processLifecycleScope

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
        BookmarkEntity::class
    ],
    version = DATABASE_VERSION
)
abstract class TokushoDatabase : RoomDatabase() {

    abstract fun getTagsDao(): TagsDao

    abstract fun getHistoryDao(): HistoryDao

    abstract fun getMangaDao(): MangaDao

    abstract fun getSourcesDao(): MangaSourcesDao

    abstract fun getFavouritesDao(): FavouritesDao

    abstract fun getFavouriteCategoriesDao(): FavouriteCategoriesDao

    abstract fun getBookmarksDao(): BookmarksDao

}

fun TokushoDatabase(context: Context): TokushoDatabase = Room
    .databaseBuilder(context, TokushoDatabase::class.java, "tokusho-db")
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