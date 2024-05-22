package org.xtimms.shirizu.sections.settings.storage

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import okhttp3.Cache
import org.xtimms.shirizu.core.cache.CacheDir
import org.xtimms.shirizu.data.LocalStorageManager
import javax.inject.Inject

class StorageScreenModel @Inject constructor(
    private val storageManager: LocalStorageManager,
    private val httpCache: Cache
) : StateScreenModel<StorageScreenModel.State>(State()) {


    init {
        screenModelScope.launch(Dispatchers.Default) {
            mutableState.update {
                it.copy(
                    availableSpace = storageManager.computeAvailableSize(),
                    pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                    thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                    httpCacheSize = runInterruptible { httpCache.size() },
                    isLoading = false
                )
            }
        }
    }

    fun clearCache(cache: CacheDir) {
        screenModelScope.launch(Dispatchers.Default) {
            try {
                storageManager.clearCache(cache)
                storageManager.computeCacheSize(cache)
                mutableState.update {
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
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val size = runInterruptible(Dispatchers.IO) {
                    httpCache.evictAll()
                    httpCache.size()
                }
                mutableState.update {
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

    fun showCleanDialog(
        pagesCache: Long,
        thumbnailsCache: Long,
        availableSpace: Long,
        httpCacheSize: Long,
    ) {
        mutableState.update {
            it.copy(
                dialog = Dialog(
                    pagesCache, thumbnailsCache, availableSpace, httpCacheSize
                )
            )
        }
    }

    fun closeDialog() {
        mutableState.update { it.copy(dialog = null) }
    }

    data class Dialog(
        val pagesCache: Long = -1L,
        val thumbnailsCache: Long = -1L,
        val availableSpace: Long = -1L,
        val httpCacheSize: Long = -1L,
    )

    @Immutable
    data class State(
        val pagesCache: Long = -1L,
        val thumbnailsCache: Long = -1L,
        val availableSpace: Long = -1L,
        val httpCacheSize: Long = -1L,
        val dialog: Dialog? = null,
        val isLoading: Boolean = true,
    )
}