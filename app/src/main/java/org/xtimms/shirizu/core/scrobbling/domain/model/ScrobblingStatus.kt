package org.xtimms.shirizu.core.scrobbling.domain.model

import org.xtimms.shirizu.core.model.ListModel

enum class ScrobblingStatus : ListModel {

    PLANNED, READING, RE_READING, COMPLETED, ON_HOLD, DROPPED;

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is ScrobblingStatus && other.ordinal == ordinal
    }
}