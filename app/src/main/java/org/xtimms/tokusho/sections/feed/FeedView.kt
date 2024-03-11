package org.xtimms.tokusho.sections.feed

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.ScaffoldWithClassicTopAppBar

const val FEED_DESTINATION = "feed"

@Composable
fun FeedView(
    navigateBack: () -> Unit,
    navigateToShelf: () -> Unit,
) {
    rememberScrollState()

    ScaffoldWithClassicTopAppBar(
        title = stringResource(R.string.feed),
        navigateBack = navigateBack,
        actions = {
            IconButton(onClick = { navigateToShelf() }) {
                Icon(imageVector = Icons.Outlined.Tune, contentDescription = null)
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ClearAll,
                    contentDescription = "Clear all"
                )
                Text(
                    text = stringResource(R.string.clear_all),
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                )
            }
        }
    ) { padding ->

    }
}