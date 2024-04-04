package org.xtimms.etsudoku.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import org.xtimms.etsudoku.core.database.entity.MangaEntity
import org.xtimms.etsudoku.core.database.entity.MangaTagsEntity
import org.xtimms.etsudoku.core.database.entity.MangaWithTags
import org.xtimms.etsudoku.core.database.entity.TagEntity

@Dao
abstract class MangaDao {

    @Transaction
    @Query("SELECT * FROM manga WHERE manga_id = :id")
    abstract suspend fun find(id: Long): MangaWithTags?

    @Transaction
    @Query("SELECT * FROM manga WHERE public_url = :publicUrl")
    abstract suspend fun findByPublicUrl(publicUrl: String): MangaWithTags?

    @Transaction
    @Query("SELECT * FROM manga WHERE source = :source")
    abstract suspend fun findAllBySource(source: String): List<MangaWithTags>

    @Upsert
    abstract suspend fun upsert(manga: MangaEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(manga: MangaEntity): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTagRelation(tag: MangaTagsEntity): Long

    @Query("DELETE FROM manga_tags WHERE manga_id = :mangaId")
    abstract suspend fun clearTagRelation(mangaId: Long)

    @Transaction
    @Delete
    abstract suspend fun delete(subjects: Collection<MangaEntity>)

    @Transaction
    open suspend fun upsert(manga: MangaEntity, tags: Iterable<TagEntity>? = null) {
        upsert(manga)
        if (tags != null) {
            clearTagRelation(manga.id)
            tags.map {
                MangaTagsEntity(manga.id, it.id)
            }.forEach {
                insertTagRelation(it)
            }
        }
    }
}