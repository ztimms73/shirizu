package org.xtimms.shirizu.core.scrobbling.domain.model

import org.xtimms.shirizu.core.model.ListModel

data class ScrobblerManga(
    val id: Long,
    val name: String,
    val altName: String?,
    val cover: String,
    val url: String,
) : ListModel {
    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is ScrobblerManga && other.id == id
    }

    override fun toString(): String {
        return "ScrobblerManga #$id \"$name\" $url"
    }
}