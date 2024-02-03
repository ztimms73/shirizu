package org.xtimms.tokusho.sections.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.components.SettingItem

const val SETTINGS_DESTINATION = "settings"

@Composable
fun SettingsView(
    navigateBack: () -> Unit,
    navigateToAppearance: () -> Unit,
    navigateToAbout: () -> Unit,
) {
    ScaffoldWithTopAppBar(
        title = stringResource(R.string.settings),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item {
                SettingItem(
                    title = stringResource(id = R.string.appearance),
                    description = stringResource(id = R.string.appearance_page),
                    icon = Icons.Outlined.Palette,
                    onClick = navigateToAppearance
                )
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.about),
                    description = stringResource(id = R.string.about_page),
                    icon = Icons.Outlined.Info,
                    onClick = navigateToAbout
                )
            }
        }
    }
}