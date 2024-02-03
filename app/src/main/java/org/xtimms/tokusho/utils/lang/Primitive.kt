package org.xtimms.tokusho.utils.lang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.xtimms.tokusho.core.prefs.AppSettings.getBoolean
import org.xtimms.tokusho.core.prefs.AppSettings.getInt
import org.xtimms.tokusho.core.prefs.AppSettings.getString

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