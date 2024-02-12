package org.xtimms.tokusho.core.parser

import android.util.Log
import coil.request.CachePolicy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import org.koitharu.kotatsu.parsers.InternalParsersApi
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.exception.ParseException
import org.koitharu.kotatsu.parsers.model.ContentRating
import org.koitharu.kotatsu.parsers.model.Favicons
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaListFilter
import org.koitharu.kotatsu.parsers.model.MangaPage
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.model.MangaState
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.koitharu.kotatsu.parsers.model.SortOrder
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.tokusho.BuildConfig
import org.xtimms.tokusho.core.cache.ContentCache
import org.xtimms.tokusho.core.cache.SafeDeferred
import org.xtimms.tokusho.utils.lang.processLifecycleScope
import java.util.Locale

@OptIn(InternalParsersApi::class)
class RemoteMangaRepository(
    private val parser: MangaParser,
    private val cache: ContentCache,
) : MangaRepository, Interceptor {

    override val source: MangaSource
        get() = parser.source

    override val sortOrders: Set<SortOrder>
        get() = parser.availableSortOrders

    override val states: Set<MangaState>
        get() = parser.availableStates

    override val contentRatings: Set<ContentRating>
        get() = parser.availableContentRating

    override val isMultipleTagsSupported: Boolean
        get() = parser.isMultipleTagsSupported

    override val isSearchSupported: Boolean
        get() = parser.isSearchSupported

    override val isTagsExclusionSupported: Boolean
        get() = parser.isTagsExclusionSupported

    val domains: Array<out String>
        get() = parser.configKeyDomain.presetValues

    val headers: Headers
        get() = parser.headers

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (parser is Interceptor) {
            parser.intercept(chain)
        } else {
            chain.proceed(chain.request())
        }
    }

    override suspend fun getList(offset: Int, filter: MangaListFilter?): List<Manga> {
        return parser.getList(offset, filter)
    }

    override suspend fun getDetails(manga: Manga): Manga = getDetails(manga, CachePolicy.ENABLED)

    override suspend fun getPages(chapter: MangaChapter): List<MangaPage> {
        cache.getPages(source, chapter.url)?.let { return it }
        val pages = asyncSafe {
            parser.getPages(chapter).distinctById()
        }
        cache.putPages(source, chapter.url, pages)
        return pages.await()
    }

    override suspend fun getPageUrl(page: MangaPage): String = parser.getPageUrl(page)

    override suspend fun getTags(): Set<MangaTag> = parser.getAvailableTags()

    override suspend fun getLocales(): Set<Locale> {
        return parser.getAvailableLocales()
    }

    suspend fun getFavicons(): Favicons = parser.getFavicons()

    override suspend fun getRelated(seed: Manga): List<Manga> {
        cache.getRelatedManga(source, seed.url)?.let { return it }
        val related = asyncSafe {
            parser.getRelatedManga(seed).filterNot { it.id == seed.id }
        }
        cache.putRelatedManga(source, seed.url, related)
        return related.await()
    }

    suspend fun getDetails(manga: Manga, cachePolicy: CachePolicy): Manga {
        if (cachePolicy.readEnabled) {
            cache.getDetails(source, manga.url)?.let { return it }
        }
        val details = asyncSafe {
            parser.getDetails(manga)
        }
        if (cachePolicy.writeEnabled) {
            cache.putDetails(source, manga.url, details)
        }
        return details.await()
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun <T> asyncSafe(block: suspend CoroutineScope.() -> T): SafeDeferred<T> {
        var dispatcher = currentCoroutineContext()[CoroutineDispatcher.Key]
        if (dispatcher == null || dispatcher is MainCoroutineDispatcher) {
            dispatcher = Dispatchers.Default
        }
        return SafeDeferred(
            processLifecycleScope.async(dispatcher) {
                runCatchingCancellable { block() }
            },
        )
    }

    private fun List<MangaPage>.distinctById(): List<MangaPage> {
        if (isEmpty()) {
            return emptyList()
        }
        val result = ArrayList<MangaPage>(size)
        val set = HashSet<Long>(size)
        for (page in this) {
            if (set.add(page.id)) {
                result.add(page)
            } else if (BuildConfig.DEBUG) {
                Log.w(null, "Duplicate page: $page")
            }
        }
        return result
    }

    private fun Result<*>.isValidResult() = exceptionOrNull() !is ParseException
            && (getOrNull() as? Collection<*>)?.isEmpty() != true
}