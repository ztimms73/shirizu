package org.xtimms.etsudoku.sections.settings.appearance

import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.xtimms.etsudoku.LocalDarkTheme
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.PreferenceSingleChoiceItem
import org.xtimms.etsudoku.core.components.PreferenceSubtitle
import org.xtimms.etsudoku.core.components.PreferenceSwitch
import org.xtimms.etsudoku.core.components.ScaffoldWithTopAppBar
import org.xtimms.etsudoku.core.prefs.AppSettings
import org.xtimms.etsudoku.core.prefs.DarkThemePreference.Companion.FOLLOW_SYSTEM
import org.xtimms.etsudoku.core.prefs.DarkThemePreference.Companion.OFF
import org.xtimms.etsudoku.core.prefs.DarkThemePreference.Companion.ON

const val DARK_THEME_DESTINATION = "dark_theme"

@Composable
fun DarkThemeView(
    navigateBack: () -> Unit
) {

    val darkThemePreference = LocalDarkTheme.current
    val isHighContrastModeEnabled = darkThemePreference.isHighContrastModeEnabled

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.dark_theme),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            if (Build.VERSION.SDK_INT >= 29)
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(id = R.string.follow_system),
                        selected = darkThemePreference.darkThemeValue == FOLLOW_SYSTEM
                    ) {
                        AppSettings.modifyDarkThemePreference(FOLLOW_SYSTEM)
                    }
                }
            item {
                PreferenceSingleChoiceItem(
                    text = stringResource(id = R.string.on),
                    selected = darkThemePreference.darkThemeValue == ON
                ) {
                    AppSettings.modifyDarkThemePreference(ON)
                }
            }
            item {
                PreferenceSingleChoiceItem(
                    text = stringResource(id = R.string.off),
                    selected = darkThemePreference.darkThemeValue == OFF
                ) {
                    AppSettings.modifyDarkThemePreference(OFF)
                }
            }
            item {
                PreferenceSubtitle(text = stringResource(R.string.additional_settings))
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.high_contrast),
                    icon = Icons.Outlined.Contrast,
                    isChecked = isHighContrastModeEnabled,
                    onClick = {
                        AppSettings.modifyDarkThemePreference(isHighContrastModeEnabled = !isHighContrastModeEnabled)
                    })
            }
        }
    }
}