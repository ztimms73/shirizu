package org.xtimms.shirizu.core.model

import androidx.core.net.toFile
import androidx.core.net.toUri
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.xtimms.shirizu.utils.system.creationTime
import java.io.File

data class LocalManga(
    val manga: Manga,
    val file: File = manga.url.toUri().toFile(),
) {

    var createdAt: Long = -1L
        private set
        get() {
            if (field == -1L) {
                field = file.creationTime
            }
            return field
        }

    fun isMatchesQuery(query: String): Boolean {
        return manga.title.contains(query, ignoreCase = true) ||
                manga.altTitle?.contains(query, ignoreCase = true) == true
    }

    fun containsTags(tags: Set<MangaTag>): Boolean {
        return manga.tags.containsAll(tags)
    }

    fun containsAnyTag(tags: Set<MangaTag>): Boolean {
        return tags.any { tag ->
            manga.tags.contains(tag)
        }
    }

    override fun toString(): String {
        return "LocalManga(${file.path}: ${manga.title})"
    }
}