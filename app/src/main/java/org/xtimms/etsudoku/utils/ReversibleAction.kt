package org.xtimms.etsudoku.utils

import androidx.annotation.StringRes

class ReversibleAction(
    @StringRes val stringResId: Int,
    val handle: ReversibleHandle?,
)