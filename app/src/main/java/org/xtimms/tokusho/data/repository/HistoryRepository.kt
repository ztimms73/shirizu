package org.xtimms.tokusho.data.repository

import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.database.MangaDatabase
import org.xtimms.tokusho.core.database.entity.HistoryEntity
import org.xtimms.tokusho.core.database.entity.toMangaHistory
import org.xtimms.tokusho.core.model.MangaHistory
import org.xtimms.tokusho.core.model.findById
import javax.inject.Inject

const val PROGRESS_NONE = -1f

@Reusable
class HistoryRepository @Inject constructor(
    private val db: MangaDatabase,
) {

    suspend fun getOne(manga: Manga): MangaHistory? {
        return db.getHistoryDao().find(manga.id)?.recoverIfNeeded(manga)?.toMangaHistory()
    }

    fun observeOne(id: Long): Flow<MangaHistory?> {
        return db.getHistoryDao().observe(id).map {
            it?.toMangaHistory()
        }
    }

    private suspend fun HistoryEntity.recoverIfNeeded(manga: Manga): HistoryEntity {
        val chapters = manga.chapters
        if (chapters.isNullOrEmpty() || chapters.findById(chapterId) != null) {
            return this
        }
        val newChapterId = chapters.getOrNull(
            (chapters.size * percent).toInt(),
        )?.id ?: return this
        val newEntity = copy(chapterId = newChapterId)
        db.getHistoryDao().update(newEntity)
        return newEntity
    }
}