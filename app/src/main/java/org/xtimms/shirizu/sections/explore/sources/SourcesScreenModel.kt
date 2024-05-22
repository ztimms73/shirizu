package org.xtimms.shirizu.sections.explore.sources

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.data.repository.MangaSourcesRepository
import org.xtimms.shirizu.utils.LocaleHelper
import javax.inject.Inject

class SourcesScreenModel @Inject constructor(
    private val mangaSourcesRepository: MangaSourcesRepository,
) : StateScreenModel<SourcesScreenModel.State>(State()) {

    private val _events = Channel<Event>(Int.MAX_VALUE)
    val events = _events.receiveAsFlow()

    init {
        screenModelScope.launch(Dispatchers.IO) {
            mangaSourcesRepository.observeEnabledSources()
                .catch {
                    it.printStackTrace()
                    _events.send(Event.InternalError)
                }
                .collectLatest(::collectEnabledSources)
        }
    }

    private fun collectEnabledSources(sources: List<MangaSource>) {
        mutableState.update { state ->
            state.copy(
                isLoading = false,
                items = sources.sortedBy { it.title }.groupBy { it.locale }
                    .toSortedMap(LocaleHelper.comparator)
                    .flatMap {
                        listOf(
                            SourceUiModel.Header(it.key),
                            *it.value.map { source ->
                                SourceUiModel.Item(source)
                            }.toTypedArray(),
                        )
                    }
                    .toImmutableList(),
            )
        }
    }

    fun hideSource(source: MangaSource) {
        screenModelScope.launch(Dispatchers.IO) {
            mangaSourcesRepository.setSourceEnabled(source, false)
        }
    }

    sealed interface Event {
        data object InternalError : Event
    }

    data class Dialog(val source: MangaSource)

    @Immutable
    data class State(
        val dialog: Dialog? = null,
        val isLoading: Boolean = true,
        val items: ImmutableList<SourceUiModel> = persistentListOf(),
    ) {
        val isEmpty = items.isEmpty()
    }

    companion object {
        const val PINNED_KEY = "pinned"
        const val LAST_USED_KEY = "last_used"
    }
}
