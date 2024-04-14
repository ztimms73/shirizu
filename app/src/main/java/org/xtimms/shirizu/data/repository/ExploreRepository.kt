package org.xtimms.shirizu.data.repository

import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaListFilter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.almostEquals
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.shirizu.core.model.TagsBlacklist
import org.xtimms.shirizu.core.parser.MangaRepository
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.utils.lang.asArrayList
import javax.inject.Inject

class ExploreRepository @Inject constructor(
    private val sourcesRepository: MangaSourcesRepository,
    private val historyRepository: HistoryRepository,
    private val mangaRepositoryFactory: MangaRepository.Factory,
) {

    suspend fun findRandomManga(tagsLimit: Int): Manga {
        val tagsBlacklist = TagsBlacklist(setOf(), 0.4f)
        val tags = historyRepository.getPopularTags(tagsLimit).mapNotNull {
            if (it in tagsBlacklist) null else it.title
        }
        val sources = sourcesRepository.getEnabledSources()
        check(sources.isNotEmpty()) { "No sources available" }
        for (i in 0..4) {
            val list = getList(sources.random(), tags, tagsBlacklist)
            val manga = list.randomOrNull() ?: continue
            val details = runCatchingCancellable {
                mangaRepositoryFactory.create(manga.source).getDetails(manga)
            }.getOrNull() ?: continue
            if ((AppSettings.isSuggestionsExcludeNsfw() && details.isNsfw) || details in tagsBlacklist) {
                continue
            }
            return details
        }
        throw NoSuchElementException()
    }

    suspend fun findRandomManga(source: MangaSource, tagsLimit: Int): Manga {
        val tagsBlacklist = TagsBlacklist(setOf(), 0.4f)
        val skipNsfw = AppSettings.isSuggestionsExcludeNsfw() && source.contentType != ContentType.HENTAI
        val tags = historyRepository.getPopularTags(tagsLimit).mapNotNull {
            if (it in tagsBlacklist) null else it.title
        }
        for (i in 0..4) {
            val list = getList(source, tags, tagsBlacklist)
            val manga = list.randomOrNull() ?: continue
            val details = runCatchingCancellable {
                mangaRepositoryFactory.create(manga.source).getDetails(manga)
            }.getOrNull() ?: continue
            if ((skipNsfw && details.isNsfw) || details in tagsBlacklist) {
                continue
            }
            return details
        }
        throw NoSuchElementException()
    }

    private suspend fun getList(
        source: MangaSource,
        tags: List<String>,
        blacklist: TagsBlacklist,
    ): List<Manga> = runCatchingCancellable {
        val repository = mangaRepositoryFactory.create(source)
        val order = repository.sortOrders.random()
        val availableTags = repository.getTags()
        val tag = tags.firstNotNullOfOrNull { title ->
            availableTags.find { x -> x.title.almostEquals(title, 0.4f) }
        }
        val list = repository.getList(
            offset = 0,
            filter = MangaListFilter.Advanced.Builder(order)
                .tags(setOfNotNull(tag))
                .build(),
        ).asArrayList()
        if (AppSettings.isSuggestionsExcludeNsfw()) {
            list.removeAll { it.isNsfw }
        }
        if (blacklist.isNotEmpty()) {
            list.removeAll { manga -> manga in blacklist }
        }
        list.shuffle()
        list
    }.onFailure {
        it.printStackTrace()
    }.getOrDefault(emptyList())

}