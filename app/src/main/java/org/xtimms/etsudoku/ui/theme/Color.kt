package org.xtimms.etsudoku.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.xtimms.etsudoku.ui.monet.a1
import org.xtimms.etsudoku.ui.monet.a2
import org.xtimms.etsudoku.ui.monet.a3

val colorMin = Color(0xFF185ED6)
val colorMax = Color(0xFFDD1414)

object FixedAccentColors {
    val primaryFixed: Color
        @Composable get() = 90.a1
    val primaryFixedDim: Color
        @Composable get() = 80.a1
    val onPrimaryFixed: Color
        @Composable get() = 10.a1
    val onPrimaryFixedVariant: Color
        @Composable get() = 30.a1
    val secondaryFixed: Color
        @Composable get() = 90.a2
    val secondaryFixedDim: Color
        @Composable get() = 80.a2
    val onSecondaryFixed: Color
        @Composable get() = 10.a2
    val onSecondaryFixedVariant: Color
        @Composable get() = 30.a2
    val tertiaryFixed: Color
        @Composable get() = 90.a3
    val tertiaryFixedDim: Color
        @Composable get() = 80.a3
    val onTertiaryFixed: Color
        @Composable get() = 10.a3
    val onTertiaryFixedVariant: Color
        @Composable get() = 30.a3
}

const val SEED = 0x1978D2