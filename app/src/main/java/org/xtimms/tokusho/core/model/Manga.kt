package org.xtimms.tokusho.core.model

import androidx.core.os.LocaleListCompat
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.utils.system.iterator
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun Collection<Manga>.distinctById() = distinctBy { it.id }

fun Collection<MangaChapter>.findById(id: Long) = find { x -> x.id == id }

fun Manga.getPreferredBranch(history: MangaHistory?): String? {
    val ch = chapters
    if (ch.isNullOrEmpty()) {
        return null
    }
    if (history != null) {
        val currentChapter = ch.findById(history.chapterId)
        if (currentChapter != null) {
            return currentChapter.branch
        }
    }
    val groups = ch.groupBy { it.branch }
    if (groups.size == 1) {
        return groups.keys.first()
    }
    for (locale in LocaleListCompat.getAdjustedDefault()) {
        val displayLanguage = locale.getDisplayLanguage(locale)
        val displayName = locale.getDisplayName(locale)
        val candidates = HashMap<String?, List<MangaChapter>>(3)
        for (branch in groups.keys) {
            if (branch != null && (
                        branch.contains(displayLanguage, ignoreCase = true) ||
                                branch.contains(displayName, ignoreCase = true)
                        )
            ) {
                candidates[branch] = groups[branch] ?: continue
            }
        }
        if (candidates.isNotEmpty()) {
            return candidates.maxBy { it.value.size }.key
        }
    }
    return groups.maxByOrNull { it.value.size }?.key
}

private val chaptersNumberFormat = DecimalFormat("#.#").also { f ->
    f.decimalFormatSymbols = DecimalFormatSymbols.getInstance().also {
        it.decimalSeparator = '.'
    }
}

fun MangaChapter.formatNumber(): String? {
    if (number <= 0f) {
        return null
    }
    return chaptersNumberFormat.format(number.toDouble())
}

val Manga.isLocal: Boolean
    get() = source == MangaSource.LOCAL