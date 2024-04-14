package org.xtimms.shirizu.core.tracker.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.xtimms.shirizu.core.exceptions.TooManyRequestExceptions
import org.xtimms.shirizu.utils.lang.ifZero

sealed interface MangaUpdates {

    val manga: Manga

    data class Success(
        override val manga: Manga,
        val newChapters: List<MangaChapter>,
        val isValid: Boolean,
        val channelId: String?,
    ) : MangaUpdates {

        fun isNotEmpty() = newChapters.isNotEmpty()

        fun lastChapterDate(): Long {
            val lastChapter = newChapters.lastOrNull()
            return if (lastChapter == null) {
                manga.chapters?.lastOrNull()?.uploadDate ?: 0L
            } else {
                lastChapter.uploadDate.ifZero { System.currentTimeMillis() }
            }
        }
    }

    data class Failure(
        override val manga: Manga,
        val error: Throwable?,
    ) : MangaUpdates {

        fun shouldRetry() = error is TooManyRequestExceptions
    }
}