package org.xtimms.etsudoku.sections.details.data

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.xtimms.etsudoku.core.model.LocalManga
import org.xtimms.etsudoku.core.model.isLocal

data class MangaDetails(
    private val manga: Manga,
    private val localManga: LocalManga?,
    val description: CharSequence?,
    val isLoaded: Boolean,
) {

    val id: Long
        get() = manga.id

    val chapters: Map<String?, List<MangaChapter>> = manga.chapters?.groupBy { it.branch }.orEmpty()

    val branches: Set<String?>
        get() = chapters.keys

    val allChapters: List<MangaChapter> by lazy { listOf() }

    val isLocal
        get() = manga.isLocal

    val local: LocalManga?
        get() = localManga ?: if (manga.isLocal) LocalManga(manga) else null

    fun toManga() = manga

    fun filterChapters(branch: String?) = MangaDetails(
        manga = manga.filterChapters(branch),
        localManga = localManga?.run {
            copy(manga = manga.filterChapters(branch))
        },
        description = description,
        isLoaded = isLoaded,
    )

    private fun mergeChapters(): List<MangaChapter> {
        val chapters = manga.chapters
        val localChapters = local?.manga?.chapters.orEmpty()
        if (chapters.isNullOrEmpty()) {
            return localChapters
        }
        val localMap = if (localChapters.isNotEmpty()) {
            localChapters.associateByTo(LinkedHashMap(localChapters.size)) { it.id }
        } else {
            null
        }
        val result = ArrayList<MangaChapter>(chapters.size)
        for (chapter in chapters) {
            val local = localMap?.remove(chapter.id)
            result += local ?: chapter
        }
        if (!localMap.isNullOrEmpty()) {
            result.addAll(localMap.values)
        }
        return result
    }
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