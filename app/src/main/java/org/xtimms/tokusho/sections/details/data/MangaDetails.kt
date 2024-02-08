package org.xtimms.tokusho.sections.details.data

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter

data class MangaDetails(
    private val manga: Manga,
    val description: CharSequence?,
    val isLoaded: Boolean,
) {

    val id: Long
        get() = manga.id

    val chapters: Map<String?, List<MangaChapter>> = manga.chapters?.groupBy { it.branch }.orEmpty()

    val branches: Set<String?>
        get() = chapters.keys

    val allChapters: List<MangaChapter> by lazy { listOf() }

    fun toManga() = manga

    fun filterChapters(branch: String?) = MangaDetails(
        manga = manga.filterChapters(branch),
        description = description,
        isLoaded = isLoaded,
    )
}

fun Manga.filterChapters(branch: String?): Manga {
    if (chapters.isNullOrEmpty()) return this
    return withChapters(chapters = chapters?.filter { it.branch == branch })
}

private fun Manga.withChapters(chapters: List<MangaChapter>?) = Manga(
    id = id,
    title = title,
    altTitle = altTitle,
    url = url,
    publicUrl = publicUrl,
    rating = rating,
    isNsfw = isNsfw,
    coverUrl = coverUrl,
    tags = tags,
    state = state,
    author = author,
    largeCoverUrl = largeCoverUrl,
    description = description,
    chapters = chapters,
    source = source,
)