package org.xtimms.tokusho.sections.shelf

import org.koitharu.kotatsu.parsers.model.Manga

data class ShelfManga(
    val manga: Manga,
) {
    val id: Long = manga.id
}