package org.xtimms.etsudoku.sections.list

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.etsudoku.core.base.state.PagedUiState

data class MangaListUiState(
    val manga: List<Manga> = listOf(),
    override val nextPage: String? = null,
    override val loadMore: Boolean = true,
    override val isLoading: Boolean = false,
    override val message: String? = null,
) : PagedUiState() {

    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setMessage(value: String?) = copy(message = value)
}