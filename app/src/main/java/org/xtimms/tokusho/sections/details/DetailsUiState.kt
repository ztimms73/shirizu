package org.xtimms.tokusho.sections.details

import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.base.state.UiState

data class DetailsUiState(
    val manga: Manga? = null,
    override val isLoading: Boolean = false,
    override val message: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setMessage(value: String?) = copy(message = value)
}