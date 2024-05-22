package org.xtimms.shirizu.sections.search.global

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.data.repository.MangaSearchRepository
import org.xtimms.shirizu.data.repository.MangaSourcesRepository
import org.xtimms.shirizu.sections.search.global.model.SearchSuggestionItem
import org.xtimms.shirizu.utils.lang.sizeOrZero
import org.xtimms.shirizu.utils.lang.toEnumSet
import javax.inject.Inject

private const val DEBOUNCE_TIMEOUT = 500L
private const val MAX_MANGA_ITEMS = 12
private const val MAX_QUERY_ITEMS = 16
private const val MAX_HINTS_ITEMS = 3
private const val MAX_AUTHORS_ITEMS = 2
private const val MAX_TAGS_ITEMS = 8
private const val MAX_SOURCES_ITEMS = 6

class GlobalSearchScreenModel @Inject constructor(
    private val repository: MangaSearchRepository,
    private val sourcesRepository: MangaSourcesRepository,
) : StateScreenModel<GlobalSearchScreenModel.State>(State()) {

    private val query = MutableStateFlow("")
    private var suggestionJob: Job? = null
    private var invalidateOnResume = false

    val suggestion = MutableStateFlow<List<SearchSuggestionItem>>(emptyList())

    init {
        setupSuggestion()
    }

    @OptIn(FlowPreview::class)
    private fun setupSuggestion() {

    }

    private suspend fun buildSearchSuggestion(
        searchQuery: String,
        enabledSources: Set<MangaSource>,
        types: Set<SearchSuggestionType>,
    ): List<SearchSuggestionItem> = coroutineScope {
        val queriesDeferred = if (SearchSuggestionType.QUERIES_RECENT in types) {
            async { repository.getQuerySuggestion(searchQuery, MAX_QUERY_ITEMS) }
        } else {
            null
        }
        val hintsDeferred = if (SearchSuggestionType.QUERIES_SUGGEST in types) {
            async { repository.getQueryHintSuggestion(searchQuery, MAX_HINTS_ITEMS) }
        } else {
            null
        }
        val authorsDeferred = if (SearchSuggestionType.AUTHORS in types) {
            async { repository.getAuthorsSuggestion(searchQuery, MAX_AUTHORS_ITEMS) }
        } else {
            null
        }
        val tagsDeferred = if (SearchSuggestionType.GENRES in types) {
            async { repository.getTagsSuggestion(searchQuery, MAX_TAGS_ITEMS, null) }
        } else {
            null
        }
        val mangaDeferred = if (SearchSuggestionType.MANGA in types) {
            async { repository.getMangaSuggestion(searchQuery, MAX_MANGA_ITEMS, null) }
        } else {
            null
        }
        val sources = if (SearchSuggestionType.SOURCES in types) {
            repository.getSourcesSuggestion(searchQuery, MAX_SOURCES_ITEMS)
        } else {
            null
        }

        val tags = tagsDeferred?.await()
        val mangaList = mangaDeferred?.await()
        val queries = queriesDeferred?.await()
        val hints = hintsDeferred?.await()
        val authors = authorsDeferred?.await()

        buildList(queries.sizeOrZero() + sources.sizeOrZero() + authors.sizeOrZero() + hints.sizeOrZero() + 2) {
            if (!mangaList.isNullOrEmpty()) {
                add(SearchSuggestionItem.MangaList(mangaList))
            }
            sources?.mapTo(this) { SearchSuggestionItem.Source(it, it in enabledSources) }
            queries?.mapTo(this) { SearchSuggestionItem.RecentQuery(it) }
            authors?.mapTo(this) { SearchSuggestionItem.Author(it) }
            hints?.mapTo(this) { SearchSuggestionItem.Hint(it) }
        }
    }

    @Immutable
    data class State(
        var isLoading: Boolean = true,
        val hints: List<SearchSuggestionItem.Hint>? = null,
    )
}