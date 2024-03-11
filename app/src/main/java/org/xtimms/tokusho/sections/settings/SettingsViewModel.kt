package org.xtimms.tokusho.sections.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import kotlinx.coroutines.runInterruptible
import okhttp3.Cache
import org.xtimms.tokusho.core.base.viewmodel.KotatsuBaseViewModel
import org.xtimms.tokusho.core.cache.CacheDir
import org.xtimms.tokusho.data.LocalStorageManager
import org.xtimms.tokusho.data.repository.MangaSourcesRepository
import org.xtimms.tokusho.sections.settings.sources.SourcesSettingsViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val storageManager: LocalStorageManager,
    private val httpCache: Cache,
    sourcesRepository: MangaSourcesRepository,
) : KotatsuBaseViewModel() {

    private var storageUsageJob: Job? = null

    private val mutableViewStateFlow = MutableStateFlow(ViewState())
    val viewStateFlow = mutableViewStateFlow.asStateFlow()

    data class ViewState(
        val pagesCache: Long = -1L,
        val thumbnailsCache: Long = -1L,
        val availableSpace: Long = -1L,
        val httpCacheSize: Long = -1L,
    )

    init {
        storageUsageJob = launchJob(Dispatchers.Default) {
            mutableViewStateFlow.update {
                it.copy(
                    availableSpace = storageManager.computeAvailableSize(),
                    pagesCache = storageManager.computeCacheSize(CacheDir.PAGES),
                    thumbnailsCache = storageManager.computeCacheSize(CacheDir.THUMBS),
                    httpCacheSize = runInterruptible { httpCache.size() }
                )
            }
        }
    }

    val totalSourcesCount = sourcesRepository.allMangaSources.size

    val enabledSourcesCount = sourcesRepository.observeEnabledSourcesCount()
        .stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, -1)
}