package org.xtimms.etsudoku.sections.suggestions

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.etsudoku.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.etsudoku.data.repository.SuggestionRepository
import org.xtimms.etsudoku.sections.history.HistoryItemModel
import org.xtimms.etsudoku.utils.lang.mapItems
import org.xtimms.etsudoku.work.suggestions.SuggestionsWorker
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