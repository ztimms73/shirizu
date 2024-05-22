package org.xtimms.shirizu.sections.shelf

import org.xtimms.shirizu.core.model.ListModel

data class ShelfCategory(
    val id: Long,
    val title: String,
    val mangaCount: Int,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is ShelfCategory && other.id == id
    }
}