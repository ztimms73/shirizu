package org.xtimms.tokusho.sections.details.model

import org.xtimms.tokusho.core.model.ListModel

data class MangaBranch(
    val name: String?,
    val count: Int,
    val isSelected: Boolean,
    val isCurrent: Boolean,
) : ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is MangaBranch && other.name == name
    }

    override fun toString(): String {
        return "$name: $count"
    }
}