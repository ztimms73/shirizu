package org.xtimms.shirizu.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.database.ShirizuDatabase
import org.xtimms.shirizu.core.database.entity.SuggestionEntity
import org.xtimms.shirizu.core.database.entity.toEntities
import org.xtimms.shirizu.core.database.entity.toEntity
import org.xtimms.shirizu.core.database.entity.toManga
import org.xtimms.shirizu.core.database.entity.toMangaTags
import org.xtimms.shirizu.core.model.MangaSuggestion
import org.xtimms.shirizu.utils.lang.mapItems
import javax.inject.Inject

class SuggestionRepository @Inject constructor(
    private val db: ShirizuDatabase,
) {

    fun observeAll(): Flow<List<Manga>> {
        return db.getSuggestionDao().observeAll().mapItems {
            it.manga.toManga(it.tags.toMangaTags())
        }
    }

    fun observeAll(limit: Int): Flow<List<Manga>> {
        return db.getSuggestionDao().observeAll(limit).mapItems {
            it.manga.toManga(it.tags.toMangaTags())
        }
    }

    suspend fun getRandom(): Manga? {
        return db.getSuggestionDao().getRandom()?.let {
            it.manga.toManga(it.tags.toMangaTags())
        }
    }

    suspend fun clear() {
        db.getSuggestionDao().deleteAll()
    }

    suspend fun isEmpty(): Boolean {
        return db.getSuggestionDao().count() == 0
    }

    suspend fun replace(suggestions: Iterable<MangaSuggestion>) {
        db.withTransaction {
            db.getSuggestionDao().deleteAll()
            suggestions.forEach { (manga, relevance) ->
                val tags = manga.tags.toEntities()
                db.getTagsDao().upsert(tags)
                db.getMangaDao().upsert(manga.toEntity(), tags)
                db.getSuggestionDao().upsert(
                    SuggestionEntity(
                        mangaId = manga.id,
                        relevance = relevance,
                        createdAt = System.currentTimeMillis(),
                    ),
                )
            }
        }
    }
}