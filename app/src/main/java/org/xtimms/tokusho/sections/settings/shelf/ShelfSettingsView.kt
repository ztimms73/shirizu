package org.xtimms.tokusho.sections.settings.shelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceItem
import org.xtimms.tokusho.core.components.PreferenceSubtitle
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.sections.shelf.ShelfViewModel

const val SHELF_SETTINGS_DESTINATION = "shelf_settings"

@Composable
fun ShelfSettingsView(
    shelfViewModel: ShelfViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToCategories: () -> Unit
) {

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
                        count = shelfViewModel.uiState.value.categories.size,
                        shelfViewModel.uiState.value.categories.size
                    ),
                    icon = Icons.Outlined.Category,
                    onClick = {
                        navigateToCategories()
                    }
                )
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