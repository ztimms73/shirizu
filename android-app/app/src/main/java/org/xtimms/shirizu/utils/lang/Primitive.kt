package org.xtimms.shirizu.utils.lang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.xtimms.shirizu.core.prefs.AppSettings.getBoolean
import org.xtimms.shirizu.core.prefs.AppSettings.getInt
import org.xtimms.shirizu.core.prefs.AppSettings.getString

inline val String.booleanState
    @Composable get() =
        remember { mutableStateOf(this.getBoolean()) }

inline val String.stringState
    @Composable get() =
        remember { mutableStateOf(this.getString()) }

inline val String.intState
    @Composable get() = remember {
        mutableIntStateOf(this.getInt())
    }

// clamp(3.5f, 6.7f) > [0.0f, 1.0f]
fun Float.clamp(min: Float, max: Float): Float = (1f - ((this.coerceIn(min, max) - min) / (max - min)))