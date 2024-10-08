package org.xtimms.shirizu.sections.reader.pager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.koitharu.kotatsu.parsers.model.MangaPage
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.model.parcelable.MangaSourceParceler

@Parcelize
@TypeParceler<MangaSource, MangaSourceParceler>
data class ReaderPage(
    val id: Long,
    val url: String,
    val preview: String?,
    val chapterId: Long,
    val index: Int,
    val source: MangaSource,
) : Parcelable {

    constructor(page: MangaPage, index: Int, chapterId: Long) : this(
        id = page.id,
        url = page.url,
        preview = page.preview,
        chapterId = chapterId,
        index = index,
        source = page.source,
    )

    fun toMangaPage() = MangaPage(
        id = id,
        url = url,
        preview = preview,
        source = source,
    )
}