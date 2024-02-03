package org.xtimms.tokusho.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.xtimms.tokusho.core.database.dao.MangaSourcesDao
import org.xtimms.tokusho.core.database.entity.MangaSourceEntity

const val DATABASE_VERSION = 1

@Database(
    entities = [
        MangaSourceEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class MangaDatabase : RoomDatabase() {

    abstract fun getSourcesDao(): MangaSourcesDao

}

fun MangaDatabase(context: Context): MangaDatabase = Room
    .databaseBuilder(context, MangaDatabase::class.java, "tokusho-db")
    .build()