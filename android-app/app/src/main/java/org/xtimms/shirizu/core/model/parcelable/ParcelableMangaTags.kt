package org.xtimms.shirizu.core.model.parcelable

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.xtimms.shirizu.utils.lang.readSerializableCompat

object MangaTagParceler : Parceler<MangaTag> {
    override fun create(parcel: Parcel) = MangaTag(
        title = requireNotNull(parcel.readString()),
        key = requireNotNull(parcel.readString()),
        source = requireNotNull(parcel.readSerializableCompat()),
    )

    override fun MangaTag.write(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(key)
        parcel.writeSerializable(source)
    }
}

@Parcelize
@TypeParceler<MangaTag, MangaTagParceler>
data class ParcelableMangaTags(val tags: Set<MangaTag>) : Parcelable
