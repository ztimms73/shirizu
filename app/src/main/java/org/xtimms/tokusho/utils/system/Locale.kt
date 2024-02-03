package org.xtimms.tokusho.utils.system

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.prefs.AppSettings.getInt
import org.xtimms.tokusho.core.prefs.LANGUAGE
import org.xtimms.tokusho.core.prefs.SYSTEM_DEFAULT
import java.util.Locale

fun LocaleListCompat.toList(): List<Locale> = List(size()) { i -> getOrThrow(i) }

fun LocaleListCompat.getOrThrow(index: Int) = get(index) ?: throw NoSuchElementException()

private fun getLanguageNumberByCode(languageCode: String) : Int =
    languageMap.entries.find { it.value == languageCode }?.key ?: SYSTEM_DEFAULT

fun getLanguageNumber(): Int {
    return if (Build.VERSION.SDK_INT >= 33)
        getLanguageNumberByCode(
            LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
        )
    else LANGUAGE.getInt()
}

@Composable
fun getLanguageDesc(language: Int = getLanguageNumber()): String {
    return stringResource(
        when (language) {
            ENGLISH -> R.string.la_en_US
            RUSSIAN -> R.string.la_ru
            else -> R.string.follow_system
        }
    )
}

// Do not modify
private const val ENGLISH = 1
private const val RUSSIAN = 2

// Sorted alphabetically
val languageMap: Map<Int, String> = mapOf(
    RUSSIAN to "ru",
)