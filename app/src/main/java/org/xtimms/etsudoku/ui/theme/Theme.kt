package org.xtimms.etsudoku.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.MaterialColors
import org.xtimms.etsudoku.ui.monet.dynamicColorScheme

fun Color.disabledIconOpacity(): Color {
    return this.copy(alpha = 0.38f)
}

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}

@Composable
fun Color.harmonizeWith(other: Color) =
    Color(MaterialColors.harmonize(this.toArgb(), other.toArgb()))

@Composable
fun Color.harmonizeWithPrimary(): Color =
    this.harmonizeWith(other = MaterialTheme.colorScheme.primary)

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

@Composable
fun EtsudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrastModeEnabled: Boolean = false,
    isDynamicColorEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        dynamicColorScheme(!darkTheme).run {
            if (isHighContrastModeEnabled && darkTheme) copy(
                surface = Color.Black,
                background = Color.Black
            )
            else this
        }
    val window = LocalView.current.context.findWindow()
    val view = LocalView.current

    window?.let {
        WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = darkTheme
    }

    rememberSystemUiController(window).setSystemBarsColor(Color.Transparent, !darkTheme, false)

    ProvideTextStyle(
        value = LocalTextStyle.current.copy(
            lineBreak = LineBreak.Paragraph,
            textDirection = TextDirection.Content
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(LocalContext.current),
            content = content
        )
    }
}

@Composable
fun PreviewThemeLight(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = dynamicColorScheme(),
        typography = Typography(LocalContext.current),
        content = content
    )
}