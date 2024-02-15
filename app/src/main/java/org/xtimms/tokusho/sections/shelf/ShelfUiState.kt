package org.xtimms.tokusho.sections.shelf

import org.xtimms.tokusho.core.base.state.UiState
import org.xtimms.tokusho.core.model.FavouriteCategory

data class ShelfUiState(
    val categories: List<FavouriteCategory> = emptyList(),
    override val isLoading: Boolean = false,
    override val message: String? = null,
) : UiState() {

    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setMessage(value: String?) = copy(message = value)
}