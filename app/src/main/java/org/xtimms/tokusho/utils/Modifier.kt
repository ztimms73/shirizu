package org.xtimms.tokusho.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import org.xtimms.tokusho.utils.material.SecondaryItemAlpha

fun Modifier.secondaryItemAlpha(): Modifier = this.alpha(SecondaryItemAlpha)