package org.xtimms.tokusho.sections.history

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.model.ListModel
import org.xtimms.tokusho.core.model.MangaHistory

data class HistoryItemModel(
    val manga: Manga,
    val history: MangaHistory,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is HistoryItemModel && other.manga.id == manga.id
    }
}