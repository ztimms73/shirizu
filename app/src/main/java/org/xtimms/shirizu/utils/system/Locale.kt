package org.xtimms.shirizu.utils.system

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import org.koitharu.kotatsu.parsers.util.toTitleCase
import org.xtimms.shirizu.R
import java.util.Locale

fun String.toLocale() = Locale(this)

fun Locale?.getDisplayName(context: Context): String = when (this) {
    null -> context.getString(R.string.multi_lang)
    Locale.ROOT -> context.getString(R.string.various_languages)
    else -> getDisplayLanguage(this).toTitleCase(this)
}

fun LocaleListCompat.toList(): List<Locale> = List(size()) { i -> getOrThrow(i) }

fun LocaleListCompat.getOrThrow(index: Int) = get(index) ?: throw NoSuchElementException()

@Composable
fun Locale?.toDisplayName(): String = this?.getDisplayName(this) ?: stringResource(
    id = R.string.follow_system
)

fun setLanguage(locale: Locale?) {
    val localeList = locale?.let {
        LocaleListCompat.create(it)
    } ?: LocaleListCompat.getEmptyLocaleList()
    AppCompatDelegate.setApplicationLocales(localeList)
}

// Do not modify
private const val ENGLISH = 1
private const val RUSSIAN = 2

val LocaleLanguageCodeMap =
    mapOf(
        Locale("en", "US") to ENGLISH,
        Locale("ru") to RUSSIAN
    )

// Sorted alphabetically
val languageMap: Map<Int, String> = mapOf(
    RUSSIAN to "ru",
)

operator fun LocaleListCompat.iterator(): ListIterator<Locale> = LocaleListCompatIterator(this)

private class LocaleListCompatIterator(private val list: LocaleListCompat) : ListIterator<Locale> {

    private var index = 0

    override fun hasNext() = index < list.size()

    override fun hasPrevious() = index > 0

    override fun next() = list.get(index++) ?: throw NoSuchElementException()

    override fun nextIndex() = index

    override fun previous() = list.get(--index) ?: throw NoSuchElementException()

    override fun previousIndex() = index - 1
}