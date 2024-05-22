package org.xtimms.shirizu.sections.suggestions

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.shirizu.data.repository.SuggestionRepository
import org.xtimms.shirizu.utils.lang.mapItems
import org.xtimms.shirizu.work.suggestions.SuggestionsWorker
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class SuggestionsScreenModel @Inject constructor(
    private val suggestionRepository: SuggestionRepository,
    private val suggestionsScheduler: SuggestionsWorker.Scheduler,
) : StateScreenModel<SuggestionsScreenModel.State>(State()) {

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    init {
        screenModelScope.launch {
            state.flatMapLatest {
                suggestionRepository.observeAll()
                    .distinctUntilChanged()
                    .filterNotNull()
                    .catch { error ->
                        error.printStackTrace()
                        _events.send(Event.InternalError)
                    }
                    .mapItems { SuggestionMangaModel(it) }
                    .flowOn(Dispatchers.IO)
            }.collect { newList ->
                mutableState.update {
                    it.copy(isLoading = false, list = newList)
                }
            }
        }
    }

    fun updateSuggestions() {
        screenModelScope.launch(Dispatchers.IO) {
            suggestionsScheduler.startNow()
            _events.send(Event.GettingSuggestions)
        }
    }

    @Immutable
    data class State(
        var isLoading: Boolean = true,
        val list: List<SuggestionMangaModel>? = null,
    )

    sealed interface Event {
        data object GettingSuggestions : Event
        data object InternalError : Event
    }

}
