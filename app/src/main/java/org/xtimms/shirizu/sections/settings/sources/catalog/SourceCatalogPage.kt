package org.xtimms.shirizu.sections.settings.sources.catalog

import org.koitharu.kotatsu.parsers.model.ContentType
import org.xtimms.shirizu.core.model.ListModel

data class SourceCatalogPage(
    val type: ContentType,
    val items: List<SourceCatalogItemModel>,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is SourceCatalogPage && other.type == type
    }
}