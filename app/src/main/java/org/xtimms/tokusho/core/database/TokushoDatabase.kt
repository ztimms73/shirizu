package org.xtimms.tokusho.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.xtimms.tokusho.core.database.dao.MangaDao
import org.xtimms.tokusho.core.database.dao.MangaSourcesDao
import org.xtimms.tokusho.core.database.entity.MangaEntity
import org.xtimms.tokusho.core.database.entity.MangaSourceEntity
import org.xtimms.tokusho.core.database.entity.MangaTagsEntity
import org.xtimms.tokusho.core.database.entity.TagEntity

const val DATABASE_VERSION = 1

@Database(
    entities = [
        MangaEntity::class,
        TagEntity::class,
        MangaTagsEntity::class,
        MangaSourceEntity::class
    ],
    version = DATABASE_VERSION
)
abstract class MangaDatabase : RoomDatabase() {

    abstract fun getMangaDao(): MangaDao

    abstract fun getSourcesDao(): MangaSourcesDao

}

fun MangaDatabase(context: Context): MangaDatabase = Room
    .databaseBuilder(context, MangaDatabase::class.java, "tokusho-db")
    .build()