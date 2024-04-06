package org.xtimms.shirizu.data.repository

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaListFilter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.almostEquals
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.shirizu.core.parser.MangaRepository
import org.xtimms.shirizu.utils.lang.asArrayList
import javax.inject.Inject

class ExploreRepository @Inject constructor(
    private val sourcesRepository: MangaSourcesRepository,
    private val historyRepository: HistoryRepository,
    private val mangaRepositoryFactory: MangaRepository.Factory,
) {

    private suspend fun getList(
        source: MangaSource,
        tags: List<String>,
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
        list.shuffle()
        list
    }.onFailure {
        it.printStackTrace()
    }.getOrDefault(emptyList())

}