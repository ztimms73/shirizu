package org.xtimms.shirizu.sections.settings.shelf.categories

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.core.model.ListModel
import org.xtimms.shirizu.core.model.ListSortOrder
import org.xtimms.shirizu.data.repository.FavouritesRepository
import java.util.ArrayList
import javax.inject.Inject

class CategoriesScreenModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
) : StateScreenModel<CategoryScreenState>(CategoryScreenState.Loading) {

    private val _events: Channel<CategoryEvent> = Channel()
    val events = _events.receiveAsFlow()

    init {
        screenModelScope.launch {
            favouritesRepository.observeCategoriesForLibrary()
                .collectLatest { categories ->
                    mutableState.update {
                        CategoryScreenState.Success(
                            categories = categories
                                .toImmutableList(),
                        )
                    }
                }
        }
    }

    fun createCategory(name: String) {
        screenModelScope.launch {
            favouritesRepository.createCategory(name, ListSortOrder.NEWEST, isTrackerEnabled = true, isVisibleOnShelf = true)
        }
    }

    fun deleteCategory(ids: Set<Long>) {
        screenModelScope.launch {
            favouritesRepository.removeCategories(ids)
        }
    }

    fun reorder(snapshot: List<FavouriteCategory>) {
        screenModelScope.launch {
            val ids = snapshot.mapTo(ArrayList(snapshot.size)) { it.id }
            if (ids.isNotEmpty()) {
                favouritesRepository.reorderCategories(ids)
            }
        }
    }

    fun renameCategory(category: FavouriteCategory, name: String) {
        screenModelScope.launch {
            favouritesRepository.updateCategory(category.id, name, ListSortOrder.NEWEST, isVisibleOnShelf = true, isTrackerEnabled = true)
        }
    }

    fun showDialog(dialog: CategoryDialog) {
        mutableState.update {
            when (it) {
                CategoryScreenState.Loading -> it
                is CategoryScreenState.Success -> it.copy(dialog = dialog)
            }
        }
    }

    fun dismissDialog() {
        mutableState.update {
            when (it) {
                CategoryScreenState.Loading -> it
                is CategoryScreenState.Success -> it.copy(dialog = null)
            }
        }
    }
}

sealed interface CategoryDialog {
    data object Create : CategoryDialog
    data class Rename(val category: FavouriteCategory) : CategoryDialog
    data class Delete(val category: FavouriteCategory) : CategoryDialog
}

sealed interface CategoryEvent {
    sealed class LocalizedMessage(@StringRes val stringRes: Int) : CategoryEvent
    data object InternalError : LocalizedMessage(R.string.error_occured)
}

sealed interface CategoryScreenState {

    @Immutable
    data object Loading : CategoryScreenState

    @Immutable
    data class Success(
        val categories: ImmutableList<FavouriteCategory>,
        val dialog: CategoryDialog? = null,
    ) : CategoryScreenState {

        val isEmpty: Boolean
            get() = categories.isEmpty()
    }
}