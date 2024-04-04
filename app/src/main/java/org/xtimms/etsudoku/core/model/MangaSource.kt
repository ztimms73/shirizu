package org.xtimms.etsudoku.core.model

import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaSource

fun MangaSource(name: String): MangaSource {
    MangaSource.entries.forEach {
        if (it.name == name) return it
    }
    return MangaSource.DUMMY
}

fun MangaSource.isNsfw() = contentType == ContentType.HENTAI