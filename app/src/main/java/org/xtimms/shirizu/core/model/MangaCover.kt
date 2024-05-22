package org.xtimms.shirizu.core.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource

data class MangaCover(
    val mangaId: Long,
    val source: MangaSource,
    val url: String?,
)

fun Manga.asMangaCover(): MangaCover {
    return MangaCover(
        mangaId = id,
        source = source,
        url = largeCoverUrl ?: coverUrl,
    )
}