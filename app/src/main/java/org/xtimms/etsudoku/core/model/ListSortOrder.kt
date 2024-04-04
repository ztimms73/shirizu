package org.xtimms.etsudoku.core.model

import androidx.annotation.StringRes
import org.xtimms.etsudoku.R
import org.koitharu.kotatsu.parsers.util.find
import java.util.EnumSet

enum class ListSortOrder(
    @StringRes val titleResId: Int,
) {

    NEWEST(R.string.order_added),
    PROGRESS(R.string.progress),
    ALPHABETIC(R.string.by_name),
    ;

    fun isGroupingSupported() = this == NEWEST || this == PROGRESS

    companion object {

        val SHELF: Set<ListSortOrder> = EnumSet.of(NEWEST, PROGRESS, ALPHABETIC)

        operator fun invoke(value: String, fallback: ListSortOrder) = entries.find(value) ?: fallback
    }
}