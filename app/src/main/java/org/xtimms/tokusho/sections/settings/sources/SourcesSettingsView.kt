package org.xtimms.tokusho.sections.settings.sources

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.NoAdultContent
import androidx.compose.material.icons.outlined.SettingsApplications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceItem
import org.xtimms.tokusho.core.components.PreferenceSwitch
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.NSFW

const val SOURCES_DESTINATION = "sources"

@Composable
fun SourcesView(
    viewModel: SourcesSettingsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToSourcesCatalog: () -> Unit,
    navigateToSourcesManagement: () -> Unit,
) {

    val context = LocalContext.current
    val availableSourcesCount = viewModel.availableSourcesCount.collectAsState(-1).value
    val enabledSourcesCount = viewModel.enabledSourcesCount.collectAsState(-1).value
    val state by viewModel.viewStateFlow.collectAsStateWithLifecycle()

    var isNSFWEnabled by remember {
        mutableStateOf(AppSettings.isNSFWEnabled())
    }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.manga_sources),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.manage_sources),
                    description = if (enabledSourcesCount >= 0) {
                        context.resources.getQuantityString(
                            R.plurals.items,
                            enabledSourcesCount,
                            enabledSourcesCount
                        )
                    } else {
                        null
                    },
                    icon = Icons.Outlined.SettingsApplications,
                    onClick = { navigateToSourcesManagement() }
                )
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.sources_catalog),
                    description = if (availableSourcesCount >= 0) {
                        stringResource(R.string.available_d, availableSourcesCount)
                    } else {
                        null
                    },
                    icon = Icons.Outlined.Apps,
                    onClick = { navigateToSourcesCatalog() }
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.disable_nsfw),
                    description = stringResource(id = R.string.disable_nsfw_desc),
                    icon = Icons.Outlined.NoAdultContent,
                    isChecked = isNSFWEnabled
                ) {
                    isNSFWEnabled = !isNSFWEnabled
                    AppSettings.updateValue(NSFW, isNSFWEnabled)
                }
            }
        }
    }
}