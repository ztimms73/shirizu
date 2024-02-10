package org.xtimms.tokusho.sections.settings.storage

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runInterruptible
import okhttp3.Cache
import org.xtimms.tokusho.core.base.viewmodel.BaseViewModel
import org.xtimms.tokusho.core.cache.CacheDir
import org.xtimms.tokusho.data.LocalStorageManager
import java.util.EnumMap
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageManager: LocalStorageManager,
    private val httpCache: Cache,
) : BaseViewModel<StorageUiState>() {

    val httpCacheSize = MutableStateFlow(-1L)
    val cacheSizes = EnumMap<CacheDir, MutableStateFlow<Long>>(CacheDir::class.java)

    init {
        launchJob(Dispatchers.Default) {
            setLoading(true)
            httpCacheSize.value = runInterruptible { httpCache.size() }
            mutableUiState.update {
                it.copy(
                    availableSpace = storageManager.computeAvailableSize(),
                    pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                    thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                    httpCacheSize = httpCacheSize.value,
                    isLoading = false
                )
            }
        }
    }

    fun clearCache(cache: CacheDir) {
        launchJob(Dispatchers.Default) {
            try {
                storageManager.clearCache(cache)
                checkNotNull(cacheSizes[cache]).value = storageManager.computeCacheSize(cache)
                mutableUiState.update {
                    it.copy(
                        availableSpace = storageManager.computeAvailableSize(),
                        pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                        thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                        httpCacheSize = httpCacheSize.value,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {

            }
        }
    }

    fun clearHttpCache() {
        launchJob(Dispatchers.Default) {
            try {
                val size = runInterruptible(Dispatchers.IO) {
                    httpCache.evictAll()
                    httpCache.size()
                }
                httpCacheSize.value = size
                mutableUiState.update {
                    it.copy(
                        availableSpace = storageManager.computeAvailableSize(),
                        pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                        thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                        httpCacheSize = httpCacheSize.value,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {

            }
        }
    }

    override val mutableUiState = MutableStateFlow(StorageUiState())

}