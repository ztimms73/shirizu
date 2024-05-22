package org.xtimms.shirizu.sections.feed

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.xtimms.shirizu.core.tracker.model.TrackingLogItem
import org.xtimms.shirizu.data.repository.TrackingRepository
import org.xtimms.shirizu.work.tracker.TrackWorker
import javax.inject.Inject

private const val PAGE_SIZE = 20

@OptIn(ExperimentalCoroutinesApi::class)
class FeedScreenModel @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val trackScheduler: TrackWorker.Scheduler,
) : StateScreenModel<FeedScreenModel.State>(State()) {

    private val limit = MutableStateFlow(PAGE_SIZE)
    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    init {
        screenModelScope.launch {
            trackingRepository.gc()
            state.flatMapLatest {
                trackingRepository.observeTrackingLog(limit)
                    .distinctUntilChanged()
                    .catch {
                        _events.send(Event.InternalError)
                    }
                    .map { it }
                    .flowOn(Dispatchers.IO)
            }
                .collect { newList -> mutableState.update { it.copy(list = newList) } }
        }
    }

    fun clearFeed(clearCounters: Boolean) {
        screenModelScope.launch(Dispatchers.Default) {
            trackingRepository.clearLogs()
            if (clearCounters) {
                trackingRepository.clearCounters()
            }
            _events.send(Event.FeedCleared)
        }
    }

    fun updateFeed() {
        trackScheduler.startNow()
    }

    @Immutable
    data class State(
        val searchQuery: String? = null,
        val list: List<TrackingLogItem>? = null,
        val dialog: Dialog? = null,
    )

    sealed interface Dialog {
        data object DeleteAll : Dialog
        data class Delete(val history: FeedScreenModel) : Dialog
    }

    sealed interface Event {
        data object InternalError : Event
        data object FeedCleared : Event
    }
}