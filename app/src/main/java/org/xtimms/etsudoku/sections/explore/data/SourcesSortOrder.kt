package org.xtimms.etsudoku.sections.explore.data

import androidx.annotation.StringRes
import org.xtimms.etsudoku.R

enum class SourcesSortOrder(
    @StringRes val titleResId: Int,
) {
    ALPHABETIC(R.string.by_name),
    POPULARITY(R.string.popular),
    MANUAL(R.string.manual),
}