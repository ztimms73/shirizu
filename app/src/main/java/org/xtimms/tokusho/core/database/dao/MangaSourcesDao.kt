package org.xtimms.tokusho.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.xtimms.tokusho.core.database.entity.MangaSourceEntity

@Dao
abstract class MangaSourcesDao {

    @Query("SELECT * FROM sources ORDER BY sort_key")
    abstract suspend fun findAll(): List<MangaSourceEntity>

    @Query("SELECT * FROM sources WHERE enabled = 0 ORDER BY sort_key")
    abstract suspend fun findAllDisabled(): List<MangaSourceEntity>

    @Query("SELECT * FROM sources WHERE enabled = 0")
    abstract fun observeDisabled(): Flow<List<MangaSourceEntity>>

    @Query("SELECT * FROM sources ORDER BY sort_key")
    abstract fun observeAll(): Flow<List<MangaSourceEntity>>

    @Query("SELECT IFNULL(MAX(sort_key),0) FROM sources")
    abstract suspend fun getMaxSortKey(): Int

    @Query("UPDATE sources SET enabled = 0")
    abstract suspend fun disableAllSources()

}