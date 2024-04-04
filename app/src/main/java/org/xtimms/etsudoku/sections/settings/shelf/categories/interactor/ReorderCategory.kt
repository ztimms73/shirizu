package org.xtimms.etsudoku.sections.settings.shelf.categories.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.xtimms.etsudoku.core.model.FavouriteCategory
import org.xtimms.etsudoku.data.repository.FavouritesRepository
import org.xtimms.etsudoku.utils.lang.processLifecycleScope
import org.xtimms.etsudoku.utils.lang.withNonCancellableContext
import java.util.Collections

class ReorderCategory(
    private val favouritesRepository: FavouritesRepository,
) {

    private val mutex = Mutex()

    suspend fun moveUp(category: FavouriteCategory): Result = await(category, MoveTo.UP)

    suspend fun moveDown(category: FavouriteCategory): Result = await(category, MoveTo.DOWN)

    private suspend fun await(category: FavouriteCategory, moveTo: MoveTo) = withNonCancellableContext {
        mutex.withLock {
            val categories = favouritesRepository.observeCategoriesForLibrary()
                .map { it.toMutableList() }
                .stateIn(processLifecycleScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList()).value

            val currentIndex = categories.indexOfFirst { it.id == category.id }
            if (currentIndex == -1) {
                return@withNonCancellableContext Result.Unchanged
            }

            val newPosition = when (moveTo) {
                MoveTo.UP -> currentIndex - 1
                MoveTo.DOWN -> currentIndex + 1
            }.toInt()

            try {
                Collections.swap(categories, currentIndex, newPosition)
                Result.Success
            } catch (e: Exception) {
                Result.InternalError(e)
            }
        }
    }

    sealed interface Result {
        data object Success : Result
        data object Unchanged : Result
        data class InternalError(val error: Throwable) : Result
    }

    private enum class MoveTo {
        UP,
        DOWN,
    }
}