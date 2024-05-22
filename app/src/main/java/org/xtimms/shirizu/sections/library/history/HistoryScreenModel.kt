package org.xtimms.shirizu.sections.library.history

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastMap
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.model.MangaHistory
import org.xtimms.shirizu.core.model.MangaWithHistory
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.data.repository.HistoryRepository
import org.xtimms.shirizu.utils.lang.addOrRemove
import org.xtimms.shirizu.utils.lang.combine
import org.xtimms.shirizu.utils.lang.insertSeparators
import org.xtimms.shirizu.utils.lang.isSameDay
import javax.inject.Inject

@OptIn(FlowPreview::class)
class HistoryScreenModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : StateScreenModel<HistoryScreenModel.State>(State()) {

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private val selectedPositions: Array<Int> = arrayOf(-1, -1)
    private val selectedMangaIds: HashSet<Long> = HashSet()

    init {
        val queryFilter: (String) -> ((MangaWithHistory) -> Boolean) = { query ->
            filter@{ mwh ->
                if (query.isEmpty()) return@filter true
                query.split(",").any { _input ->
                    val input = _input.trim()
                    if (input.isEmpty()) return@any false
                    mwh.manga.title.contains(input, ignoreCase = true)
                }
            }
        }
        screenModelScope.launch {
            combine(
                historyRepository.observeAllWithHistory().distinctUntilChanged()
                    .catch { _events.send(Event.InternalError) },
                state.map { it.searchQuery }.distinctUntilChanged().debounce(150L),
                state.map { it.showNsfw }.distinctUntilChanged(),
                state.map { it.sort }.distinctUntilChanged()
            ) { history, query, nsfw, sort ->
                val searchQuery = query ?: ""
                history.asSequence().map { it }
                    .filter { it.manga.isNsfw == nsfw }
                    .sortedByDescending {
                        when (sort) {
                            SortOption.DATE_ADDED -> it.history.updatedAt
                            SortOption.ALPHABETICAL -> it.manga.title.lowercase()
                        }.toString()
                    }
                    .filter(queryFilter(searchQuery)).toList()
                    .toImmutableList()
            }.collectLatest {
                mutableState.update { state ->
                    state.copy(
                        isLoading = false,
                        list = it.toHistoryItemModels(),
                    )
                }
            }
        }
    }

    private fun List<MangaWithHistory>.toHistoryItemModels(): PersistentList<HistoryItemModel> {
        return this
            .map { history ->
                HistoryItemModel(
                    manga = history.manga,
                    history = history.history,
                    selected = history.manga.id in selectedMangaIds,
                )
            }
            .toPersistentList()
    }

    fun search(query: String?) {
        mutableState.update {
            it.copy(searchQuery = query)
        }
    }

    fun sort(sort: SortOption) {
        mutableState.update {
            it.copy(sort = sort)
        }
    }

    fun filterNsfw(enabled: Boolean) {
        mutableState.update {
            it.copy(showNsfw = enabled)
        }
    }

    fun removeFromHistory(ids: Set<Long>) {
        if (ids.isEmpty()) {
            return
        }
        screenModelScope.launch(Dispatchers.Default) {
            historyRepository.delete(ids)
            _events.send(Event.HistoryCleared)
        }
    }

    fun removeFromHistory(manga: Manga) {
        screenModelScope.launch(Dispatchers.Default) {
            historyRepository.delete(manga)
            _events.send(Event.HistoryCleared)
        }
    }

    fun openDeleteMangaDialog() {
        val mangaList = state.value.selection.map { it }
        mutableState.update { it.copy(dialog = Dialog.Delete(mangaList)) }
    }

    fun toggleSelection(
        item: HistoryItemModel,
        selected: Boolean,
        userSelected: Boolean = false,
        fromLongPress: Boolean = false,
    ) {
        mutableState.update { state ->
            val newItems = state.list.toMutableList().apply {
                val selectedIndex = indexOfFirst { it.manga.id == item.manga.id }
                if (selectedIndex < 0) return@apply

                val selectedItem = get(selectedIndex)
                if (selectedItem.selected == selected) return@apply

                val firstSelection = none { it.selected }
                set(selectedIndex, selectedItem.copy(selected = selected))
                selectedMangaIds.addOrRemove(item.manga.id, selected)

                if (selected && userSelected && fromLongPress) {
                    if (firstSelection) {
                        selectedPositions[0] = selectedIndex
                        selectedPositions[1] = selectedIndex
                    } else {
                        // Try to select the items in-between when possible
                        val range: IntRange
                        if (selectedIndex < selectedPositions[0]) {
                            range = selectedIndex + 1..<selectedPositions[0]
                            selectedPositions[0] = selectedIndex
                        } else if (selectedIndex > selectedPositions[1]) {
                            range = (selectedPositions[1] + 1)..<selectedIndex
                            selectedPositions[1] = selectedIndex
                        } else {
                            // Just select itself
                            range = IntRange.EMPTY
                        }

                        range.forEach {
                            val inbetweenItem = get(it)
                            if (!inbetweenItem.selected) {
                                selectedMangaIds.add(inbetweenItem.manga.id)
                                set(it, inbetweenItem.copy(selected = true))
                            }
                        }
                    }
                } else if (userSelected && !fromLongPress) {
                    if (!selected) {
                        if (selectedIndex == selectedPositions[0]) {
                            selectedPositions[0] = indexOfFirst { it.selected }
                        } else if (selectedIndex == selectedPositions[1]) {
                            selectedPositions[1] = indexOfLast { it.selected }
                        }
                    } else {
                        if (selectedIndex < selectedPositions[0]) {
                            selectedPositions[0] = selectedIndex
                        } else if (selectedIndex > selectedPositions[1]) {
                            selectedPositions[1] = selectedIndex
                        }
                    }
                }
            }
            state.copy(list = newItems.toPersistentList())
        }
    }

    fun clearSelection() {
        mutableState.update { it.copy(selection = persistentListOf()) }
    }

    fun closeDialog() {
        mutableState.update { it.copy(dialog = null) }
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val searchQuery: String? = null,
        val selection: PersistentList<Manga> = persistentListOf(),
        val showNsfw: Boolean = AppSettings.showNsfwInHistory(),
        val availableSorts: List<SortOption> = listOf(
            SortOption.DATE_ADDED,
            SortOption.ALPHABETICAL
        ),
        val sort: SortOption = SortOption.ALPHABETICAL,
        val list: PersistentList<HistoryItemModel> = persistentListOf(),
        val dialog: Dialog? = null,
    ) {
        val isEmpty = list.isEmpty()

        val selected = list.filter { it.selected }
        val selectionMode = selected.isNotEmpty()

        fun getUiModel(): List<HistoryUiModel> {
            return list
                .map { HistoryUiModel.Item(it) }
                .takeIf { sort == SortOption.DATE_ADDED }
                ?.insertSeparators { before, after ->
                    val beforeDate = before?.item?.history?.createdAt
                    val afterDate = after?.item?.history?.createdAt
                    when {
                        beforeDate != afterDate && afterDate != null -> HistoryUiModel.Header(
                            afterDate
                        )
                        // Return null to avoid adding a separator between two items.
                        else -> null
                    }
                }
                ?: list.map { HistoryUiModel.Item(it) }
        }
    }

    sealed interface Dialog {
        data object DeleteAll : Dialog
        data class Delete(val history: List<Manga>) : Dialog
    }

    sealed interface Event {
        data object InternalError : Event
        data object HistoryCleared : Event
    }

}