package org.xtimms.etsudoku.data.repository

import androidx.room.withTransaction
import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.etsudoku.core.database.EtsudokuDatabase
import org.xtimms.etsudoku.core.database.entity.FavouriteCategoryEntity
import org.xtimms.etsudoku.core.database.entity.FavouriteEntity
import org.xtimms.etsudoku.core.database.entity.toEntities
import org.xtimms.etsudoku.core.database.entity.toEntity
import org.xtimms.etsudoku.core.database.entity.toFavouriteCategory
import org.xtimms.etsudoku.core.database.entity.toManga
import org.xtimms.etsudoku.core.database.entity.toMangaList
import org.xtimms.etsudoku.core.model.FavouriteCategory
import org.xtimms.etsudoku.core.model.ListSortOrder
import org.xtimms.etsudoku.utils.ReversibleHandle
import org.xtimms.etsudoku.utils.lang.mapItems
import javax.inject.Inject

@Reusable
class FavouritesRepository @Inject constructor(
    private val db: EtsudokuDatabase,
) {

    suspend fun getAllManga(): List<Manga> {
        val entities = db.getFavouritesDao().findAll()
        return entities.toMangaList()
    }

    suspend fun getLastManga(limit: Int): List<Manga> {
        val entities = db.getFavouritesDao().findLast(limit)
        return entities.toMangaList()
    }

    fun observeAll(categoryId: Long, order: ListSortOrder): Flow<List<Manga>> {
        return db.getFavouritesDao().observeAll(categoryId, order)
            .mapItems { it.toManga() }
    }

    suspend fun getManga(categoryId: Long): List<Manga> {
        val entities = db.getFavouritesDao().findAll(categoryId)
        return entities.toMangaList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAll(categoryId: Long): Flow<List<Manga>> {
        return observeOrder(categoryId)
            .flatMapLatest { order -> observeAll(categoryId, order) }
    }

    fun observeMangaCount(): Flow<Int> {
        return db.getFavouritesDao().observeMangaCount()
            .distinctUntilChanged()
    }

    fun observeMangaCountInCategory(categoryId: Long): Flow<Int> {
        return db.getFavouritesDao().observeMangaCountInCategory(categoryId)
            .distinctUntilChanged()
    }

    fun observeCategories(): Flow<List<FavouriteCategory>> {
        return db.getFavouriteCategoriesDao().observeAll().mapItems {
            it.toFavouriteCategory()
        }.distinctUntilChanged()
    }

    fun observeCategoriesForLibrary(): Flow<List<FavouriteCategory>> {
        return db.getFavouriteCategoriesDao().observeAllForLibrary().mapItems {
            it.toFavouriteCategory()
        }.distinctUntilChanged()
    }

    fun observeCategoriesIds(mangaId: Long): Flow<Set<Long>> {
        return db.getFavouritesDao().observeIds(mangaId).map { it.toSet() }
    }

    suspend fun getCategory(id: Long): FavouriteCategory {
        return db.getFavouriteCategoriesDao().find(id.toInt()).toFavouriteCategory()
    }

    suspend fun getCategoriesIds(mangaIds: Collection<Long>): Set<Long> {
        return db.getFavouritesDao().findCategoriesIds(mangaIds).toSet()
    }

    suspend fun createCategory(
        title: String,
        sortOrder: ListSortOrder,
        isTrackerEnabled: Boolean,
        isVisibleOnShelf: Boolean,
    ): FavouriteCategory {
        val entity = FavouriteCategoryEntity(
            title = title,
            createdAt = System.currentTimeMillis(),
            sortKey = db.getFavouriteCategoriesDao().getNextSortKey(),
            categoryId = 0,
            order = sortOrder.name,
            track = isTrackerEnabled,
            deletedAt = 0L,
            isVisibleInLibrary = isVisibleOnShelf,
        )
        val id = db.getFavouriteCategoriesDao().insert(entity)
        return entity.toFavouriteCategory(id)
    }

    suspend fun updateCategory(
        id: Long,
        title: String,
        sortOrder: ListSortOrder,
        isTrackerEnabled: Boolean,
        isVisibleOnShelf: Boolean,
    ) {
        db.getFavouriteCategoriesDao().update(id, title, sortOrder.name, isTrackerEnabled, isVisibleOnShelf)
    }

    suspend fun updateCategory(id: Long, isVisibleInLibrary: Boolean) {
        db.getFavouriteCategoriesDao().updateLibVisibility(id, isVisibleInLibrary)
    }

    suspend fun updateCategoryTracking(id: Long, isTrackingEnabled: Boolean) {
        db.getFavouriteCategoriesDao().updateTracking(id, isTrackingEnabled)
    }

    suspend fun removeCategories(ids: Collection<Long>) {
        db.withTransaction {
            for (id in ids) {
                db.getFavouritesDao().deleteAll(id)
                db.getFavouriteCategoriesDao().delete(id)
            }
        }
    }

    suspend fun setCategoryOrder(id: Long, order: ListSortOrder) {
        db.getFavouriteCategoriesDao().updateOrder(id, order.name)
    }

    suspend fun reorderCategories(orderedIds: List<Long>) {
        val dao = db.getFavouriteCategoriesDao()
        db.withTransaction {
            for ((i, id) in orderedIds.withIndex()) {
                dao.updateSortKey(id, i)
            }
        }
    }

    suspend fun addToCategory(categoryId: Long, mangas: Collection<Manga>) {
        db.withTransaction {
            for (manga in mangas) {
                val tags = manga.tags.toEntities()
                db.getTagsDao().upsert(tags)
                db.getMangaDao().upsert(manga.toEntity(), tags)
                val entity = FavouriteEntity(
                    mangaId = manga.id,
                    categoryId = categoryId,
                    createdAt = System.currentTimeMillis(),
                    sortKey = 0,
                    deletedAt = 0L,
                )
                db.getFavouritesDao().insert(entity)
            }
        }
    }

    suspend fun removeFromFavourites(ids: Collection<Long>): ReversibleHandle {
        db.withTransaction {
            for (id in ids) {
                db.getFavouritesDao().delete(mangaId = id)
            }
        }
        return ReversibleHandle { recoverToFavourites(ids) }
    }

    suspend fun removeFromCategory(categoryId: Long, ids: Collection<Long>): ReversibleHandle {
        db.withTransaction {
            for (id in ids) {
                db.getFavouritesDao().delete(categoryId = categoryId, mangaId = id)
            }
        }
        return ReversibleHandle { recoverToCategory(categoryId, ids) }
    }

    private fun observeOrder(categoryId: Long): Flow<ListSortOrder> {
        return db.getFavouriteCategoriesDao().observe(categoryId)
            .filterNotNull()
            .map { x -> ListSortOrder(x.order, ListSortOrder.NEWEST) }
            .distinctUntilChanged()
    }

    private suspend fun recoverToFavourites(ids: Collection<Long>) {
        db.withTransaction {
            for (id in ids) {
                db.getFavouritesDao().recover(mangaId = id)
            }
        }
    }

    private suspend fun recoverToCategory(categoryId: Long, ids: Collection<Long>) {
        db.withTransaction {
            for (id in ids) {
                db.getFavouritesDao().recover(mangaId = id, categoryId = categoryId)
            }
        }
    }
}