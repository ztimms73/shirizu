package org.xtimms.shirizu.sections.explore.catalog

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.model.getTitle
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.data.repository.MangaSourcesRepository
import org.xtimms.shirizu.sections.explore.sources.SourceUiModel
import org.xtimms.shirizu.utils.LocaleHelper
import org.xtimms.shirizu.utils.lang.combine
import javax.inject.Inject

@OptIn(FlowPreview::class)
class CatalogScreenModel @Inject constructor(
    private val mangaSourcesRepository: MangaSourcesRepository,
) : StateScreenModel<CatalogScreenModel.State>(State()) {

    private val _events = Channel<Event>(Int.MAX_VALUE)
    val events = _events.receiveAsFlow()

    init {
        val queryFilter: (String) -> ((MangaParserSource) -> Boolean) = { query ->
            filter@{ source ->
                if (query.isEmpty()) return@filter true
                query.split(",").any { _input ->
                    val input = _input.trim()
                    if (input.isEmpty()) return@any false
                    source.title.contains(input, ignoreCase = true)
                }
            }
        }
        screenModelScope.launch(Dispatchers.IO) {
            mangaSourcesRepository.assimilateNewSources()
            combine(
                mangaSourcesRepository.observeDisabledSources(),
                state.map { it.searchQuery }.distinctUntilChanged().debounce(150L),
                state.map { it.mangaSourcesEnabled }.distinctUntilChanged(),
                state.map { it.hentaiSourcesEnabled }.distinctUntilChanged(),
                state.map { it.comicsSourcesEnabled }.distinctUntilChanged(),
                state.map { it.otherSourcesEnabled }.distinctUntilChanged()
            ) { sources, query, m, h, c, o ->
                val searchQuery = query ?: ""
                sources.sortedBy { it.title }
                    .filter {
                        when (it.contentType) {
                            ContentType.MANGA -> m
                            ContentType.HENTAI -> h
                            ContentType.COMICS -> c
                            ContentType.OTHER -> o
                        }
                    }
                    .filter(queryFilter(searchQuery))
                    .groupBy { it.locale }
                    .toSortedMap(LocaleHelper.comparator)
                    .flatMap {
                        listOf(
                            SourceUiModel.Header(it.key),
                            *it.value.map { source ->
                                SourceUiModel.Item(source)
                            }.toTypedArray()
                        )
                    }.toImmutableList()
            }.collectLatest {
                mutableState.update { state ->
                    state.copy(
                        isLoading = false,
                        items = it,
                    )
                }
            }
        }
    }

    fun enableSource(source: MangaSource) {
        screenModelScope.launch(Dispatchers.IO) {
            mangaSourcesRepository.setSourceEnabled(source, true)
        }
    }

    fun search(query: String?) {
        mutableState.update {
            it.copy(searchQuery = query)
        }
    }

    fun filterMangaSources(enabled: Boolean) {
        mutableState.update {
            it.copy(mangaSourcesEnabled = enabled)
        }
    }

    fun filterHentaiSources(enabled: Boolean) {
        mutableState.update {
            it.copy(hentaiSourcesEnabled = enabled)
        }
    }

    fun filterComicsSources(enabled: Boolean) {
        mutableState.update {
            it.copy(comicsSourcesEnabled = enabled)
        }
    }

    fun filterOtherSources(enabled: Boolean) {
        mutableState.update {
            it.copy(otherSourcesEnabled = enabled)
        }
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val items: ImmutableList<SourceUiModel> = persistentListOf(),
        val searchQuery: String? = null,
        val mangaSourcesEnabled: Boolean = AppSettings.isMangaContentTypeEnabled(),
        val hentaiSourcesEnabled: Boolean = AppSettings.isHentaiContentTypeEnabled(),
        val comicsSourcesEnabled: Boolean = AppSettings.isComicsContentTypeEnabled(),
        val otherSourcesEnabled: Boolean = AppSettings.isOtherContentTypeEnabled()
    ) {
        val isEmpty = items.isEmpty()
    }

    sealed interface Event {
        data object InternalError : Event
    }

}
