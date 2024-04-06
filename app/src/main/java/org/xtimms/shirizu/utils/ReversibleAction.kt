package org.xtimms.shirizu.utils

import androidx.annotation.StringRes

class ReversibleAction(
    @StringRes val stringResId: Int,
    val handle: ReversibleHandle?,
)