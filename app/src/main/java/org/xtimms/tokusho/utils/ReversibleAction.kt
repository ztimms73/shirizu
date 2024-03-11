package org.xtimms.tokusho.utils

import androidx.annotation.StringRes

class ReversibleAction(
    @StringRes val stringResId: Int,
    val handle: ReversibleHandle?,
)