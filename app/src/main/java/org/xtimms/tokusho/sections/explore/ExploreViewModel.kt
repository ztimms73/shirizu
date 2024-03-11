package org.xtimms.tokusho.sections.explore

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.core.parser.favicon.faviconUri
import org.xtimms.tokusho.data.repository.ExploreRepository
import org.xtimms.tokusho.data.repository.MangaSourcesRepository
import org.xtimms.tokusho.utils.lang.mapItems
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val exploreRepository: ExploreRepository,
    private val mangaSourcesRepository: MangaSourcesRepository,
) : KotatsuBaseViewModel() {

    private val sourcesStateFlow = mangaSourcesRepository.observeEnabledSources()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, null)

    val content = sourcesStateFlow
        .filterNotNull()
        .mapItems { SourceItemModel(it.ordinal, it.name, it.title, it.faviconUri()) }
        .distinctUntilChanged()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, emptyList())

    private fun createContentFlow() = combine(
        mangaSourcesRepository.observeEnabledSources(),
        mangaSourcesRepository.observeNewSources(),
    ) { content, newSources ->
        buildList(content, newSources)
    }

    private fun buildList(
        sources: List<MangaSource>,
        newSources: Set<MangaSource>,
    ): List<MangaSource> {
        val result = ArrayList<MangaSource>(sources.size + 3)
        return result
    }

}