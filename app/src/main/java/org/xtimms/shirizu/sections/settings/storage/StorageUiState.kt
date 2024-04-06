package org.xtimms.shirizu.sections.settings.storage

import org.xtimms.shirizu.core.base.state.UiState

data class StorageUiState(
    val pagesCache: Long = -1L,
    val thumbnailsCache: Long = -1L,
    val availableSpace: Long = -1L,
    val httpCacheSize: Long = -1L,
    override val isLoading: Boolean = false,
    override val message: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setMessage(value: String?) = copy(message = value)
}