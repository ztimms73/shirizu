package org.xtimms.shirizu.core.tracker.model

import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.xtimms.shirizu.core.exceptions.TooManyRequestExceptions

sealed interface MangaUpdates {

    val manga: Manga

    data class Success(
        override val manga: Manga,
        val newChapters: List<MangaChapter>,
        val isValid: Boolean,
        val channelId: String?,
    ) : MangaUpdates {

        fun isNotEmpty() = newChapters.isNotEmpty()
    }

    data class Failure(
        override val manga: Manga,
        val error: Throwable?,
    ) : MangaUpdates {

        fun shouldRetry() = error is TooManyRequestExceptions
    }
}