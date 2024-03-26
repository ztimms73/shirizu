package org.xtimms.tokusho.sections.history

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.data.repository.HistoryRepository
import org.xtimms.tokusho.utils.lang.mapItems
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
) : KotatsuBaseViewModel() {

    private val historyStateFlow = repository.observeAllWithHistory()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val content = historyStateFlow
        .filterNotNull()
        .mapItems { HistoryItemModel(it.manga, it.history) }
        .distinctUntilChanged()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    fun removeFromHistory(history: HistoryItemModel) {
        launchJob(Dispatchers.Default) {
            repository.delete(history.manga)
        }
    }
}