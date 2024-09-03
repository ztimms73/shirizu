package org.xtimms.shirizu.core.model

import android.content.Context
import androidx.annotation.StringRes
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.util.splitTwoParts
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.parser.external.ExternalMangaSource
import org.xtimms.shirizu.utils.system.getDisplayName
import org.xtimms.shirizu.utils.system.toLocale

data object LocalMangaSource : MangaSource {
    override val name = "LOCAL"
}

data object UnknownMangaSource : MangaSource {
    override val name = "UNKNOWN"
}

fun MangaSource(name: String?): MangaSource {
    when (name ?: return UnknownMangaSource) {
        UnknownMangaSource.name -> return UnknownMangaSource

        LocalMangaSource.name -> return LocalMangaSource
    }
    if (name.startsWith("content:")) {
        val parts = name.substringAfter(':').splitTwoParts('/') ?: return UnknownMangaSource
        return ExternalMangaSource(packageName = parts.first, authority = parts.second)
    }
    MangaParserSource.entries.forEach {
        if (it.name == name) return it
    }
    return UnknownMangaSource
}

fun MangaSource.isNsfw(): Boolean = when (this) {
    is MangaSourceInfo -> mangaSource.isNsfw()
    is MangaParserSource -> contentType == ContentType.HENTAI
    else -> false
}

@get:StringRes
val ContentType.titleResId
    get() = when (this) {
        ContentType.MANGA -> R.string.content_type_manga
        ContentType.HENTAI -> R.string.hentai
        ContentType.COMICS -> R.string.comics
        ContentType.OTHER -> R.string.other_source
    }

fun MangaSource.getTitle(context: Context): String = when (this) {
    is MangaSourceInfo -> mangaSource.getTitle(context)
    is MangaParserSource -> title
    LocalMangaSource -> context.getString(R.string.local_storage)
    is ExternalMangaSource -> resolveName(context)
    else -> context.getString(R.string.unknown)
}

fun MangaSource.getSummary(context: Context): String? = when (this) {
    is MangaSourceInfo -> mangaSource.getSummary(context)
    is MangaParserSource -> {
        val type = context.getString(contentType.titleResId)
        val locale = locale.toLocale().getDisplayName(context)
        context.getString(R.string.source_summary_pattern, type, locale)
    }

    is ExternalMangaSource -> context.getString(R.string.external_source)

    else -> null
}