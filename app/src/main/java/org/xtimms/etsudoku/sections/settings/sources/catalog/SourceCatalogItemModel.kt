package org.xtimms.etsudoku.sections.settings.sources.catalog

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.etsudoku.core.model.ListModel

data class SourceCatalogItemModel(
    val source: MangaSource,
    val showSummary: Boolean
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is SourceCatalogItemModel && other.source == source
    }
}
