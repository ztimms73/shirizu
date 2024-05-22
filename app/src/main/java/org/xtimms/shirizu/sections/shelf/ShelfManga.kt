package org.xtimms.shirizu.sections.shelf

import org.koitharu.kotatsu.parsers.model.Manga

data class ShelfManga(
    val manga: Manga,
    val category: Long,
) {
    val id: Long = manga.id
}