package org.xtimms.tokusho.sections.reader.domain

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.AnyThread
import androidx.collection.LongSparseArray
import androidx.collection.set
import androidx.core.net.toFile
import androidx.core.net.toUri
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koitharu.kotatsu.parsers.model.MangaPage
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.core.cache.PagesCache
import org.xtimms.tokusho.core.network.CommonHeaders
import org.xtimms.tokusho.core.network.MangaHttpClient
import org.xtimms.tokusho.core.network.interceptors.ImageProxyInterceptor
import org.xtimms.tokusho.core.parser.MangaRepository
import org.xtimms.tokusho.core.parser.RemoteMangaRepository
import org.xtimms.tokusho.core.parser.local.isFileUri
import org.xtimms.tokusho.core.parser.local.isZipUri
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.sections.reader.pager.ReaderPage
import org.xtimms.tokusho.utils.FileSize
import org.xtimms.tokusho.utils.RetainedLifecycleCoroutineScope
import org.xtimms.tokusho.utils.lang.getCompletionResultOrNull
import org.xtimms.tokusho.utils.lang.withProgress
import org.xtimms.tokusho.utils.progress.ProgressDeferred
import org.xtimms.tokusho.utils.system.URI_SCHEME_ZIP
import org.xtimms.tokusho.utils.system.compressToPNG
import org.xtimms.tokusho.utils.system.ensureRamAtLeast
import org.xtimms.tokusho.utils.system.ensureSuccess
import org.xtimms.tokusho.utils.system.exists
import org.xtimms.tokusho.utils.system.isPowerSaveMode
import org.xtimms.tokusho.utils.system.isTargetNotEmpty
import org.xtimms.tokusho.utils.system.ramAvailable
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipFile
import javax.inject.Inject
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@ActivityRetainedScoped
class PageLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    lifecycle: ActivityRetainedLifecycle,
    @MangaHttpClient private val okHttp: OkHttpClient,
    private val cache: PagesCache,
    private val mangaRepositoryFactory: MangaRepository.Factory,
    private val imageProxyInterceptor: ImageProxyInterceptor,
) {

    val loaderScope = RetainedLifecycleCoroutineScope(lifecycle) + InternalErrorHandler() + Dispatchers.Default

    private val tasks = LongSparseArray<ProgressDeferred<Uri, Float>>()
    private val semaphore = Semaphore(3)
    private val convertLock = Mutex()
    private val prefetchLock = Mutex()

    @Volatile
    private var repository: MangaRepository? = null
    private val prefetchQueue = LinkedList<MangaPage>()
    private val counter = AtomicInteger(0)
    private var prefetchQueueLimit = PREFETCH_LIMIT_DEFAULT // TODO adaptive

    fun isPrefetchApplicable(): Boolean {
        return repository is RemoteMangaRepository
                // && settings.isPagesPreloadEnabled
                && !context.isPowerSaveMode()
                && !isLowRam()
    }

    @AnyThread
    fun prefetch(pages: List<ReaderPage>) = loaderScope.launch {
        prefetchLock.withLock {
            for (page in pages.asReversed()) {
                if (tasks.containsKey(page.id)) {
                    continue
                }
                prefetchQueue.offerFirst(page.toMangaPage())
                if (prefetchQueue.size > prefetchQueueLimit) {
                    prefetchQueue.pollLast()
                }
            }
        }
        if (counter.get() == 0) {
            onIdle()
        }
    }

    fun loadPageAsync(page: MangaPage, force: Boolean): ProgressDeferred<Uri, Float> {
        var task = tasks[page.id]?.takeIf { it.isValid() }
        if (force) {
            task?.cancel()
        } else if (task?.isCancelled == false) {
            return task
        }
        task = loadPageAsyncImpl(page, force)
        synchronized(tasks) {
            tasks[page.id] = task
        }
        return task
    }

    suspend fun loadPage(page: MangaPage, force: Boolean): Uri {
        return loadPageAsync(page, force).await()
    }

    suspend fun convertBitmap(uri: Uri): Uri = convertLock.withLock {
        if (uri.isZipUri()) {
            val bitmap = runInterruptible(Dispatchers.IO) {
                ZipFile(uri.schemeSpecificPart).use { zip ->
                    val entry = zip.getEntry(uri.fragment)
                    context.ensureRamAtLeast(entry.size * 2)
                    zip.getInputStream(zip.getEntry(uri.fragment)).use {
                        BitmapFactory.decodeStream(it)
                    }
                }
            }
            cache.put(uri.toString(), bitmap).toUri()
        } else {
            val file = uri.toFile()
            context.ensureRamAtLeast(file.length() * 2)
            val image = runInterruptible(Dispatchers.IO) {
                BitmapFactory.decodeFile(file.absolutePath)
            }
            try {
                image.compressToPNG(file)
            } finally {
                image.recycle()
            }
            uri
        }
    }

    suspend fun getPageUrl(page: MangaPage): String {
        return getRepository(page.source).getPageUrl(page)
    }

    private fun onIdle() = loaderScope.launch {
        prefetchLock.withLock {
            while (prefetchQueue.isNotEmpty()) {
                val page = prefetchQueue.pollFirst() ?: return@launch
                if (cache.get(page.url) == null) {
                    synchronized(tasks) {
                        tasks[page.id] = loadPageAsyncImpl(page, false)
                    }
                    return@launch
                }
            }
        }
    }

    private fun loadPageAsyncImpl(page: MangaPage, skipCache: Boolean): ProgressDeferred<Uri, Float> {
        val progress = MutableStateFlow(PROGRESS_UNDEFINED)
        val deferred = loaderScope.async {
            if (!skipCache) {
                cache.get(page.url)?.let { return@async it.toUri() }
            }
            counter.incrementAndGet()
            try {
                loadPageImpl(page, progress)
            } finally {
                if (counter.decrementAndGet() == 0) {
                    onIdle()
                }
            }
        }
        return ProgressDeferred(deferred, progress)
    }

    @Synchronized
    private fun getRepository(source: MangaSource): MangaRepository {
        val result = repository
        return if (result != null && result.source == source) {
            result
        } else {
            mangaRepositoryFactory.create(source).also { repository = it }
        }
    }

    private suspend fun loadPageImpl(page: MangaPage, progress: MutableStateFlow<Float>): Uri = semaphore.withPermit {
        val pageUrl = getPageUrl(page)
        check(pageUrl.isNotBlank()) { "Cannot obtain full image url for $page" }
        val uri = Uri.parse(pageUrl)
        return when {
            uri.isZipUri() -> if (uri.scheme == URI_SCHEME_ZIP) {
                uri
            } else { // legacy uri
                uri.buildUpon().scheme(URI_SCHEME_ZIP).build()
            }

            uri.isFileUri() -> uri
            else -> {
                val request = createPageRequest(page, pageUrl)
                imageProxyInterceptor.interceptPageRequest(request, okHttp).ensureSuccess().use { response ->
                    val body = checkNotNull(response.body) { "Null response body" }
                    body.withProgress(progress).use {
                        cache.put(pageUrl, it.source())
                    }
                }.toUri()
            }
        }
    }

    private fun isLowRam(): Boolean {
        return context.ramAvailable <= FileSize.MEGABYTES.convert(PREFETCH_MIN_RAM_MB, FileSize.BYTES)
    }

    private fun Deferred<Uri>.isValid(): Boolean {
        return getCompletionResultOrNull()?.map { uri ->
            uri.exists() && uri.isTargetNotEmpty()
        }?.getOrDefault(false) ?: true
    }

    private class InternalErrorHandler : AbstractCoroutineContextElement(CoroutineExceptionHandler),
        CoroutineExceptionHandler {

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            exception.printStackTrace()
        }
    }

    companion object {

        private const val PROGRESS_UNDEFINED = -1f
        private const val PREFETCH_LIMIT_DEFAULT = 6
        private const val PREFETCH_MIN_RAM_MB = 80L

        fun createPageRequest(page: MangaPage, pageUrl: String) = Request.Builder()
            .url(pageUrl)
            .get()
            .header(CommonHeaders.ACCEPT, "image/webp,image/png;q=0.9,image/jpeg,*/*;q=0.8")
            .cacheControl(CommonHeaders.CACHE_CONTROL_NO_STORE)
            .tag(MangaSource::class.java, page.source)
            .build()
    }
}