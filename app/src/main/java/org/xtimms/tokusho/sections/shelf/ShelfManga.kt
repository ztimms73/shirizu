package org.xtimms.tokusho.sections.shelf

import org.koitharu.kotatsu.parsers.model.Manga

data class ShelfManga(
    val manga: Manga,
    val category: Long,
    val totalChapters: Long,
    val readCount: Long,
    val bookmarkCount: Long,
    val latestUpload: Long,
    val chapterFetchedAt: Long,
    val lastRead: Long,
) {
    val id: Long = manga.id

    val unreadCount
        get() = totalChapters - readCount

    val hasBookmarks
        get() = bookmarkCount > 0

    val hasStarted = readCount > 0
}