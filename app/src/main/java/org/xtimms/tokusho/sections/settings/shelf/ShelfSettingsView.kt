package org.xtimms.tokusho.sections.settings.shelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Update
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceItem
import org.xtimms.tokusho.core.components.PreferenceSubtitle
import org.xtimms.tokusho.core.components.PreferenceSwitch
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.TABS_MANGA_COUNT
import org.xtimms.tokusho.sections.shelf.ShelfViewModel

const val SHELF_SETTINGS_DESTINATION = "shelf_settings"

@Composable
fun ShelfSettingsView(
    shelfViewModel: ShelfViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToCategories: () -> Unit
) {

    val categories by shelfViewModel.categories.collectAsStateWithLifecycle(emptyList())

    var isMangaCountInTabsEnabled by remember {
        mutableStateOf(AppSettings.isMangaCountInTabsEnabled())
    }

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

}