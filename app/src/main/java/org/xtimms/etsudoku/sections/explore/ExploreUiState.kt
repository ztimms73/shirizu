package org.xtimms.etsudoku.sections.explore

import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.etsudoku.core.base.state.UiState

data class ExploreUiState(
    val sources: List<MangaSource> = emptyList(),
    val coil: ImageLoader? = null,
    override val isLoading: Boolean = false,
    override val message: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setMessage(value: String?) = copy(message = value)
}