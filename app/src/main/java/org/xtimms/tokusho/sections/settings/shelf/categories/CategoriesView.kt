package org.xtimms.tokusho.sections.settings.shelf.categories

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.ScaffoldWithClassicTopAppBar

const val CATEGORIES_DESTINATION = "categories"

@Composable
fun CategoriesView(
    navigateBack: () -> Unit,
) {

    ScaffoldWithClassicTopAppBar(
        title = stringResource(R.string.edit_categories),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Outlined.NewLabel,
                    contentDescription = "New category"
                )
                Text(
                    text = stringResource(R.string.add),
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                )
            }
        },
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {

        }
    }

}