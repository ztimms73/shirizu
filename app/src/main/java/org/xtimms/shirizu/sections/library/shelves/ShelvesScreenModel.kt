package org.xtimms.shirizu.sections.library.shelves

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.data.repository.FavouritesRepository
import org.xtimms.shirizu.sections.library.history.HistoryItemModel
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ShelvesScreenModel @Inject constructor(
    private val favouritesRepository: FavouritesRepository,
) : StateScreenModel<ShelvesScreenModel.State>(State()) {

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    init {
        screenModelScope.launch {
            state.flatMapLatest {
                favouritesRepository.observeCategories()
                    .distinctUntilChanged()
                    .catch {
                        _events.send(Event.InternalError)
                    }
                    .map { it }
                    .flowOn(Dispatchers.IO)
            }
                .collect { newList ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            list = newList.toImmutableList(),
                        )
                    }
                }
        }

        screenModelScope.launch {
            state.flatMapLatest {
                favouritesRepository.observeMangaCount()
                    .distinctUntilChanged()
                    .map { it }
                    .flowOn(Dispatchers.IO)
            }.collect { count ->
                mutableState.update {
                    it.copy(
                        mangaCount = count
                    )
                }
            }
        }
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val searchQuery: String? = null,
        val list: ImmutableList<FavouriteCategory> = persistentListOf(),
        val mangaCount: Int = 0,
        val dialog: Dialog? = null,
    ) {
        val isEmpty = list.isEmpty()
    }

    sealed interface Dialog {
        data class Delete(val history: HistoryItemModel) : Dialog
    }

    sealed interface Event {
        data object InternalError : Event
    }
}