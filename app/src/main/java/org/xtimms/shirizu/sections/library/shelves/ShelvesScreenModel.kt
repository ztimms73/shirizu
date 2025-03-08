package org.xtimms.shirizu.sections.library.shelves

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.shirizu.core.model.Cover
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.data.repository.FavouritesRepository
import org.xtimms.shirizu.sections.library.history.HistoryItemModel
import javax.inject.Inject

class ShelvesScreenModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
) : StateScreenModel<ShelvesScreenModel.State>(State()) {

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    init {
        screenModelScope.launch {
            favouritesRepository.observeCategoriesWithCovers()
                .distinctUntilChanged()
                .flowOn(Dispatchers.IO)
                .catch { _events.send(Event.InternalError) }
                .map { map ->
                    map.map { (category, covers) ->
                        CategoryItem(
                            category = category,
                            covers = covers.take(3),
                            mangaCount = covers.size
                        )
                    }
                        .sortedBy { it.category.order }
                        .toImmutableList()
                }
                .collect { categoryItems  ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            categories = categoryItems
                        )
                    }
                }
        }
    }

    @Immutable
    data class State(
        val categories: ImmutableList<CategoryItem> = persistentListOf(),
        val isLoading: Boolean = true,
        val dialog: Dialog? = null,
    ) {
        val isEmpty = categories.isEmpty()
    }

    @Immutable
    data class CategoryItem(
        val category: FavouriteCategory,
        val covers: List<Cover>,
        val mangaCount: Int
    )

    sealed interface Dialog {
        data class Delete(val history: HistoryItemModel) : Dialog
    }

    sealed interface Event {
        data object InternalError : Event
    }
}