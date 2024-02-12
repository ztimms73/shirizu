package org.xtimms.tokusho.core.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter

fun Collection<Manga>.distinctById() = distinctBy { it.id }

fun Collection<MangaChapter>.findById(id: Long) = find { x -> x.id == id }