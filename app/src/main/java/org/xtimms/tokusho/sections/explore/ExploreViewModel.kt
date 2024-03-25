package org.xtimms.tokusho.sections.explore

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.core.parser.favicon.faviconUri
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.data.repository.ExploreRepository
import org.xtimms.tokusho.data.repository.MangaSourcesRepository
import org.xtimms.tokusho.data.repository.SuggestionRepository
import org.xtimms.tokusho.utils.lang.mapItems
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val suggestionRepository: SuggestionRepository,
    private val exploreRepository: ExploreRepository,
    private val mangaSourcesRepository: MangaSourcesRepository,
) : KotatsuBaseViewModel() {

    private val isSuggestionsEnabled = MutableStateFlow(AppSettings.isSuggestionsEnabled()).asStateFlow()

    private val sourcesStateFlow = mangaSourcesRepository.observeEnabledSources()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val content = sourcesStateFlow
        .filterNotNull()
        .mapItems { SourceItemModel(it.ordinal, it.name, it.title, it.faviconUri()) }
        .distinctUntilChanged()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    private fun createContentFlow() = combine(
        mangaSourcesRepository.observeEnabledSources(),
        getSuggestionFlow(),
        mangaSourcesRepository.observeNewSources(),
    ) { content, suggestions, newSources ->
        buildList(content, suggestions, newSources)
    }

    private fun buildList(
        sources: List<MangaSource>,
        recommendation: Manga?,
        newSources: Set<MangaSource>,
    ): List<MangaSource> {
        val result = ArrayList<MangaSource>(sources.size + 3)
        if (recommendation != null) {

        }
        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSuggestionFlow() = isSuggestionsEnabled.mapLatest { isEnabled ->
        if (isEnabled) {
            runCatchingCancellable {
                suggestionRepository.getRandom()
            }.getOrNull()
        } else {
            null
        }
    }
}