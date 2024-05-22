package org.xtimms.shirizu

import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.xtimms.shirizu.core.logs.FileLogger
import org.xtimms.shirizu.ui.theme.SEED
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.DarkThemePreference
import org.xtimms.shirizu.core.prefs.paletteStyles
import org.xtimms.shirizu.core.scrobbling.services.kitsu.data.KitsuRepository
import org.xtimms.shirizu.core.scrobbling.services.shikimori.data.ShikimoriRepository
import org.xtimms.shirizu.ui.monet.LocalTonalPalettes
import org.xtimms.shirizu.ui.monet.PaletteStyle
import org.xtimms.shirizu.ui.monet.TonalPalettes.Companion.toTonalPalettes

data class BottomSheetScrollState(
    val topPadding: Dp,
)

val LocalBottomSheetScrollState = compositionLocalOf { BottomSheetScrollState(0.dp) }
val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { SEED }
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalPaletteStyleIndex = compositionLocalOf { 0 }
val LocalWindowInsets = compositionLocalOf { PaddingValues(0.dp) }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
val LocalImageLoader = compositionLocalOf<ImageLoader> { error("No ImageLoader provided") }
val LocalLoggers = compositionLocalOf<Set<@JvmSuppressWildcards FileLogger>> { error("No file loggers provided") }

val LocalKitsuRepository = compositionLocalOf<KitsuRepository> { error("No KitsuRepository provided") }
val LocalShikimoriRepository = compositionLocalOf<ShikimoriRepository> { error("No ShikimoriRepository provided") }

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