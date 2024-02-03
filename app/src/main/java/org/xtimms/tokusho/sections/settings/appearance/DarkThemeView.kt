package org.xtimms.tokusho.sections.settings.appearance

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.xtimms.tokusho.LocalDarkTheme
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceSingleChoiceItem
import org.xtimms.tokusho.core.components.PreferenceSubtitle
import org.xtimms.tokusho.core.components.PreferenceSwitch
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.DarkThemePreference.Companion.FOLLOW_SYSTEM
import org.xtimms.tokusho.core.prefs.DarkThemePreference.Companion.OFF
import org.xtimms.tokusho.core.prefs.DarkThemePreference.Companion.ON

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
            modifier = Modifier.padding(padding)) {
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