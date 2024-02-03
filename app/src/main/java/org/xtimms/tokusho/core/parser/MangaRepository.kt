package org.xtimms.tokusho.core.parser

import androidx.annotation.AnyThread
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.model.ContentRating
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaListFilter
import org.koitharu.kotatsu.parsers.model.MangaPage
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.model.MangaState
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.koitharu.kotatsu.parsers.model.SortOrder
import org.xtimms.tokusho.core.cache.ContentCache
import java.lang.ref.WeakReference
import java.util.EnumMap
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface MangaRepository {

    val source: MangaSource

    val sortOrders: Set<SortOrder>

    val states: Set<MangaState>

    val contentRatings: Set<ContentRating>

    val isMultipleTagsSupported: Boolean

    val isTagsExclusionSupported: Boolean

    val isSearchSupported: Boolean

    suspend fun getList(offset: Int, filter: MangaListFilter?): List<Manga>

    suspend fun getDetails(manga: Manga): Manga

    suspend fun getPages(chapter: MangaChapter): List<MangaPage>

    suspend fun getPageUrl(page: MangaPage): String

    suspend fun getTags(): Set<MangaTag>

    suspend fun getLocales(): Set<Locale>

    suspend fun getRelated(seed: Manga): List<Manga>

    @Singleton
    class Factory @Inject constructor(
        private val loaderContext: MangaLoaderContext,
        private val contentCache: ContentCache,
    ) {

        private val cache = EnumMap<MangaSource, WeakReference<RemoteMangaRepository>>(MangaSource::class.java)

        @AnyThread
        fun create(source: MangaSource): MangaRepository {
            cache[source]?.get()?.let { return it }
            return synchronized(cache) {
                cache[source]?.get()?.let { return it }
                val repository = RemoteMangaRepository(
                    parser = MangaParser(source, loaderContext),
                    cache = contentCache,
                )
                cache[source] = WeakReference(repository)
                repository
            }
        }
    }
}