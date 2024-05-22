package org.xtimms.shirizu.core.scrobbling.domain.model

import org.xtimms.shirizu.core.model.ListModel

data class ScrobblingInfo(
    val scrobbler: ScrobblerService,
    val mangaId: Long,
    val targetId: Long,
    val status: ScrobblingStatus?,
    val chapter: Int,
    val comment: String?,
    val rating: Float,
    val title: String,
    val coverUrl: String,
    val description: CharSequence?,
    val externalUrl: String,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is ScrobblingInfo && other.scrobbler == scrobbler
    }
}