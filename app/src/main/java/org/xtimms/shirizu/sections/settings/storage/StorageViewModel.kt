package org.xtimms.shirizu.sections.settings.storage

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runInterruptible
import okhttp3.Cache
import org.xtimms.shirizu.core.base.viewmodel.BaseViewModel
import org.xtimms.shirizu.core.cache.CacheDir
import org.xtimms.shirizu.data.LocalStorageManager
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val storageManager: LocalStorageManager,
    private val httpCache: Cache,
) : BaseViewModel<StorageUiState>(), StorageEvent {

    private var storageUsageJob: Job? = null

    init {
        storageUsageJob = launchJob(Dispatchers.Default) {
            setLoading(true)
            mutableUiState.update {
                it.copy(
                    availableSpace = storageManager.computeAvailableSize(),
                    pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                    thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                    httpCacheSize = runInterruptible { httpCache.size() },
                    isLoading = false
                )
            }
            setLoading(false)
        }
    }

    fun clearCache(cache: CacheDir) {
        launchJob(Dispatchers.Default) {
            try {
                storageManager.clearCache(cache)
                storageManager.computeCacheSize(cache)
                mutableUiState.update {
                    it.copy(
                        availableSpace = storageManager.computeAvailableSize(),
                        pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                        thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                        httpCacheSize = runInterruptible { httpCache.size() },
                        isLoading = false,
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
                mutableUiState.update {
                    it.copy(
                        availableSpace = storageManager.computeAvailableSize(),
                        pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                        thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                        httpCacheSize = size,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {

            }
        }
    }

    override val mutableUiState = MutableStateFlow(StorageUiState())

}