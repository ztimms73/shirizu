package org.xtimms.shirizu.sections.settings.shelf

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferenceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitch
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.AppSettings.getInt
import org.xtimms.shirizu.core.prefs.AppSettings.getString
import org.xtimms.shirizu.core.prefs.GRID_COLUMNS
import org.xtimms.shirizu.core.prefs.PROXY_ADDRESS
import org.xtimms.shirizu.core.prefs.TABS_MANGA_COUNT
import org.xtimms.shirizu.sections.shelf.ShelfViewModel

const val SHELF_SETTINGS_DESTINATION = "shelf_settings"

@Composable
fun ShelfSettingsView(
    shelfViewModel: ShelfViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToCategories: () -> Unit
) {

    var showGridColumnsDialog by remember { mutableStateOf(false) }

    val categories by shelfViewModel.categories.collectAsStateWithLifecycle(emptyList())

    var isMangaCountInTabsEnabled by remember {
        mutableStateOf(AppSettings.isMangaCountInTabsEnabled())
    }
    var gridColumns by remember(showGridColumnsDialog) { mutableIntStateOf(GRID_COLUMNS.getInt()) }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.nav_shelf),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.categories))
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.edit_categories),
                    description = pluralStringResource(
                        id = R.plurals.categories_count,
                        count = categories.size,
                        categories.size
                    ),
                    icon = Icons.Outlined.Category,
                    onClick = {
                        navigateToCategories()
                    }
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.show_manga_count_in_tabs),
                    icon = Icons.Outlined.Numbers,
                    isChecked = isMangaCountInTabsEnabled,
                    onClick = {
                        isMangaCountInTabsEnabled = !isMangaCountInTabsEnabled
                        AppSettings.updateValue(TABS_MANGA_COUNT, isMangaCountInTabsEnabled)
                    })
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.grid_columns_count),
                    description = stringResource(id = R.string.grid_columns_count_desc, gridColumns),
                    icon = Icons.Outlined.GridView
                ) { showGridColumnsDialog = true }
            }
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.updates))
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.auto_update),
                    description = "Off",
                    icon = Icons.Outlined.Update
                )
            }
        }
    }

    if (showGridColumnsDialog) {
        GridColumnsDialog(
            gridCount = gridColumns.toFloat()
        ) {
            showGridColumnsDialog = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridColumnsDialog(
    gridCount: Float,
    onDismissRequest: () -> Unit,
) {
    var count by remember { mutableFloatStateOf(gridCount) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                AppSettings.encodeInt(GRID_COLUMNS, count.toInt())
            }) {
                Text(stringResource(R.string.confirm))
            }
        },
        icon = { Icon(Icons.Outlined.GridView, null) },
        title = { Text(stringResource(R.string.grid_columns_count)) },
        text = {
            Column {
                val interactionSource = remember { MutableInteractionSource() }
                Text(text = stringResource(R.string.grid_columns_count_desc, count.toInt()))

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = count,
                    onValueChange = { count = it },
                    steps = 3,
                    valueRange = 1f..5f,
                    thumb = {
                        SliderDefaults.Thumb(
                            modifier = Modifier,
                            interactionSource = interactionSource,
                        )
                    }
                )
            }
        })
}