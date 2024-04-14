package org.xtimms.shirizu.core.model

import org.koitharu.kotatsu.parsers.model.Manga

data class MangaWithHistory(
    val manga: Manga,
    val history: MangaHistory
)