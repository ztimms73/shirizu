package org.xtimms.tokusho.sections.suggestions

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.data.repository.SuggestionRepository
import org.xtimms.tokusho.sections.history.HistoryItemModel
import org.xtimms.tokusho.utils.lang.mapItems
import org.xtimms.tokusho.work.suggestions.SuggestionsWorker
import javax.inject.Inject

@HiltViewModel
class SuggestionsViewModel @Inject constructor(
    repository: SuggestionRepository,
    private val suggestionsScheduler: SuggestionsWorker.Scheduler,
) : KotatsuBaseViewModel() {

    private val suggestionsStateFlow = repository.observeAll()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val content = suggestionsStateFlow
        .filterNotNull()
        .mapItems { SuggestionMangaModel(it) }
        .distinctUntilChanged()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())


    fun updateSuggestions() {
        suggestionsScheduler.startNow()
    }
}