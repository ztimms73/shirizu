package org.xtimms.tokusho.sections.shelf

import org.xtimms.tokusho.core.model.ListModel

data class FavouriteTabModel(
    val id: Long,
    val title: String,
    val mangaCount: Int,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is FavouriteTabModel && other.id == id
    }
}