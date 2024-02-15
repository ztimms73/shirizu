package org.xtimms.tokusho.data.repository

import androidx.room.withTransaction
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.database.TokushoDatabase
import org.xtimms.tokusho.core.database.entity.toFavouriteCategory
import org.xtimms.tokusho.core.database.entity.toManga
import org.xtimms.tokusho.core.model.FavouriteCategory
import org.xtimms.tokusho.core.model.ListSortOrder
import org.xtimms.tokusho.utils.ReversibleHandle
import org.xtimms.tokusho.utils.lang.mapItems
import javax.inject.Inject

@Reusable
class FavouritesRepository @Inject constructor(
    private val db: TokushoDatabase,
) {

    fun observeAll(categoryId: Long, order: ListSortOrder): Flow<List<Manga>> {
        return db.getFavouritesDao().observeAll(categoryId, order)
            .mapItems { it.toManga() }
    }

    fun observeAll(categoryId: Long): Flow<List<Manga>> {
        return observeOrder(categoryId)
            .flatMapLatest { order -> observeAll(categoryId, order) }
    }

    fun observeMangaCount(): Flow<Int> {
        return db.getFavouritesDao().observeMangaCount()
            .distinctUntilChanged()
    }

    fun observeCategoriesForLibrary(): Flow<List<FavouriteCategory>> {
        return db.getFavouriteCategoriesDao().observeAllForLibrary().mapItems {
            it.toFavouriteCategory()
        }.distinctUntilChanged()
    }

    fun observeCategoriesIds(mangaId: Long): Flow<Set<Long>> {
        return db.getFavouritesDao().observeIds(mangaId).map { it.toSet() }
    }

    suspend fun getCategoriesIds(mangaIds: Collection<Long>): Set<Long> {
        return db.getFavouritesDao().findCategoriesIds(mangaIds).toSet()
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