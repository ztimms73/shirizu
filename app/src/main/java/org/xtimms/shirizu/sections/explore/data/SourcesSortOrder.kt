package org.xtimms.shirizu.sections.explore.data

import androidx.annotation.StringRes
import org.xtimms.shirizu.R

enum class SourcesSortOrder(
    @StringRes val titleResId: Int,
) {
    ALPHABETIC(R.string.by_name),
    POPULARITY(R.string.popular),
    MANUAL(R.string.manual),
}