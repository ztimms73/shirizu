package org.xtimms.tokusho.sections.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.components.SettingItem
import org.xtimms.tokusho.sections.settings.storage.StorageViewModel
import org.xtimms.tokusho.utils.FileSize

const val SETTINGS_DESTINATION = "settings"

@Composable
fun SettingsView(
    navigateBack: () -> Unit,
    navigateToAppearance: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToAdvanced: () -> Unit,
    navigateToStorage: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: StorageViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                val allCaches = uiState.availableSpace +
                        uiState.httpCacheSize +
                        uiState.pagesCache +
                        uiState.thumbnailsCache
                val desc = buildString {
                    append((uiState.availableSpace / allCaches) * 100)
                    append("% used")
                    append(" - ")
                    append(
                        FileSize.BYTES.freeFormat(
                            context,
                            uiState.availableSpace -
                                    uiState.httpCacheSize -
                                    uiState.pagesCache -
                                    uiState.thumbnailsCache
                        )
                    )
                }
                SettingItem(
                    title = stringResource(id = R.string.storage),
                    description = desc,
                    icon = Icons.Outlined.Storage,
                    onClick = navigateToStorage
                )
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.advanced),
                    description = stringResource(id = R.string.advanced_page),
                    icon = Icons.Outlined.Code,
                    onClick = navigateToAdvanced
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