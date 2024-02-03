package org.xtimms.tokusho

import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.xtimms.shiki.ui.theme.SEED
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.DarkThemePreference
import org.xtimms.tokusho.core.prefs.paletteStyles
import org.xtimms.tokusho.ui.monet.LocalTonalPalettes
import org.xtimms.tokusho.ui.monet.PaletteStyle
import org.xtimms.tokusho.ui.monet.TonalPalettes.Companion.toTonalPalettes

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { SEED }
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalPaletteStyleIndex = compositionLocalOf { 0 }

@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    AppSettings.AppSettingsStateFlow.collectAsState().value.run {
        CompositionLocalProvider(
            LocalDarkTheme provides darkTheme,
            LocalSeedColor provides seedColor,
            LocalPaletteStyleIndex provides paletteStyleIndex,
            LocalTonalPalettes provides if (isDynamicColorEnabled && Build.VERSION.SDK_INT >= 31) dynamicDarkColorScheme(
                LocalContext.current
            ).toTonalPalettes()
            else Color(seedColor).toTonalPalettes(
                paletteStyles.getOrElse(paletteStyleIndex) { PaletteStyle.TonalSpot }
            ),
            LocalDynamicColorSwitch provides isDynamicColorEnabled,
            content = content
        )
    }
}