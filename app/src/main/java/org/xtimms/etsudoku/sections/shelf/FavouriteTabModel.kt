package org.xtimms.etsudoku.sections.shelf

import org.xtimms.etsudoku.core.model.ListModel

data class FavouriteTabModel(
    val id: Long,
    val title: String,
    val mangaCount: Int,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is FavouriteTabModel && other.id == id
    }
}