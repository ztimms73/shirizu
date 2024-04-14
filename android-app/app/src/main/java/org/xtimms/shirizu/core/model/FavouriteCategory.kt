package org.xtimms.shirizu.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class FavouriteCategory(
    val id: Long,
    val title: String,
    val sortKey: Int,
    val order: ListSortOrder,
    val createdAt: Instant,
    val isTrackingEnabled: Boolean,
    val isVisibleInLibrary: Boolean,
) : Parcelable, ListModel {

    override fun areItemsTheSame(other: ListModel): Boolean {
        return other is FavouriteCategory && id == other.id
    }
}