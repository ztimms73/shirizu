package org.xtimms.shirizu.sections.library.history

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.model.ListModel
import org.xtimms.shirizu.core.model.MangaHistory

data class HistoryItemModel(
    val manga: Manga,
    val history: MangaHistory,
    val selected: Boolean,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is HistoryItemModel && other.manga.id == manga.id
    }
}