package org.xtimms.shirizu.utils

import android.content.Context
import androidx.core.os.LocaleListCompat
import org.xtimms.shirizu.R
import org.xtimms.shirizu.sections.explore.sources.SourcesScreenModel
import java.util.Locale

object LocaleHelper {

    /**
     * Sorts by display name, except keeps the "all" (displayed as "Multi") locale at the top.
     */
    val comparator = { a: String?, b: String? ->
        if (a == "all") {
            -1
        } else if (b == "all") {
            1
        } else {
            getLocalizedDisplayName(a).compareTo(getLocalizedDisplayName(b))
        }
    }

    /**
     * Returns display name of a string language code.
     */
    fun getSourceDisplayName(lang: String?, context: Context): String {
        return when (lang) {
            SourcesScreenModel.LAST_USED_KEY -> context.resources.getString(R.string.last_used_source)
            SourcesScreenModel.PINNED_KEY -> context.resources.getString(R.string.pinned_sources)
            "other" -> context.resources.getString(R.string.other_source)
            "all" -> context.resources.getString(R.string.multi_lang)
            "null" -> context.resources.getString(R.string.multi_lang)
            else -> getLocalizedDisplayName(lang)
        }
    }

    /**
     * Returns display name of a string language code.
     *
     * @param lang empty for system language
     */
    fun getLocalizedDisplayName(lang: String?): String {
        if (lang == null) {
            return ""
        }

        val locale = when (lang) {
            "" -> LocaleListCompat.getAdjustedDefault()[0]
            "zh-CN" -> Locale.forLanguageTag("zh-Hans")
            "zh-TW" -> Locale.forLanguageTag("zh-Hant")
            else -> Locale.forLanguageTag(lang)
        }
        return locale!!.getDisplayName(locale).replaceFirstChar { it.uppercase(locale) }
    }
}