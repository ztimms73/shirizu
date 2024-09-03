package org.xtimms.shirizu.core.parser

import android.util.Log
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
import org.koitharu.kotatsu.parsers.util.domain
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.shirizu.BuildConfig
import org.xtimms.shirizu.core.cache.MemoryContentCache
import org.xtimms.shirizu.core.cache.SafeDeferred
import org.xtimms.shirizu.core.prefs.SourceSettings
import org.xtimms.shirizu.utils.lang.processLifecycleScope
import java.util.Locale

@OptIn(InternalParsersApi::class)
class ParserMangaRepository(
    private val parser: MangaParser,
    private val cache: MemoryContentCache,
) : CachingMangaRepository(cache), Interceptor {

    override val source: MangaSource
        get() = parser.source

    override val sortOrders: Set<SortOrder>
        get() = parser.availableSortOrders

    override val states: Set<MangaState>
        get() = parser.availableStates

    override val contentRatings: Set<ContentRating>
        get() = parser.availableContentRating

    override var defaultSortOrder: SortOrder
        get() = getConfig().defaultSortOrder ?: sortOrders.first()
        set(value) {
            getConfig().defaultSortOrder = value
        }

    override val isMultipleTagsSupported: Boolean
        get() = parser.isMultipleTagsSupported

    override val isSearchSupported: Boolean
        get() = parser.isSearchSupported

    override val isTagsExclusionSupported: Boolean
        get() = parser.isTagsExclusionSupported

    var domain: String
        get() = parser.domain
        set(value) {
            getConfig()[parser.configKeyDomain] = value
        }

    val domains: Array<out String>
        get() = parser.configKeyDomain.presetValues

    val headers: Headers
        get() = parser.getRequestHeaders()

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

    override suspend fun getPagesImpl(
        chapter: MangaChapter
    ): List<MangaPage> = parser.getPages(chapter)

    override suspend fun getPageUrl(page: MangaPage): String = parser.getPageUrl(page)

    override suspend fun getTags(): Set<MangaTag> = parser.getAvailableTags()

    override suspend fun getLocales(): Set<Locale> {
        return parser.getAvailableLocales()
    }

    suspend fun getFavicons(): Favicons = parser.getFavicons()

    override suspend fun getRelatedMangaImpl(seed: Manga): List<Manga> = parser.getRelatedManga(seed)

    override suspend fun getDetailsImpl(manga: Manga): Manga = parser.getDetails(manga)

    fun getAvailableMirrors(): List<String> {
        return parser.configKeyDomain.presetValues.toList()
    }

    fun isSlowdownEnabled(): Boolean {
        return getConfig().isSlowdownEnabled
    }

    private fun getConfig() = parser.config as SourceSettings

    private fun Result<*>.isValidResult() = isSuccess && (getOrNull() as? Collection<*>)?.isEmpty() != true
}