package org.xtimms.shirizu.core.scrobbling.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.xtimms.shirizu.R

enum class ScrobblerService(
    val id: Int,
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int,
) {
    SHIKIMORI(1, R.string.shikimori, R.drawable.ic_shikimori),
    KITSU(2, R.string.kitsu, R.drawable.ic_kitsu)
}