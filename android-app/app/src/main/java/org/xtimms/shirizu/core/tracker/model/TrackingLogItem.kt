package org.xtimms.shirizu.core.tracker.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.model.ListModel
import java.time.Instant

data class TrackingLogItem(
    val id: Long,
    val manga: Manga,
    val chapters: List<String>,
    val createdAt: Instant,
    val isNew: Boolean,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is TrackingLogItem && other.manga.id == manga.id
    }

}