package org.xtimms.tokusho.sections.settings.sources.catalog

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.core.model.ListModel

sealed interface SourceCatalogItemModel : ListModel {

    data class Source(
        val source: MangaSource,
        val showSummary: Boolean,
    ) : SourceCatalogItemModel {

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is Source && other.source == source
        }
    }

    data class Hint(
        val icon: ImageVector,
        @StringRes val title: Int,
        @StringRes val text: Int,
    ) : SourceCatalogItemModel {

        override fun areItemsTheSame(other: ListModel): Boolean {
            return other is Hint && other.title == title
        }
    }
}
