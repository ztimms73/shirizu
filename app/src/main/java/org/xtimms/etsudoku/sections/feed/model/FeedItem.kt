package org.xtimms.etsudoku.sections.feed.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.etsudoku.core.model.ListModel

data class FeedItem(
    val id: Long,
    val imageUrl: String,
    val title: String,
    val manga: Manga,
    val count: Int,
    val isNew: Boolean,
) : ListModel {
    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is FeedItem && other.id == id
    }
}