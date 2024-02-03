package org.xtimms.tokusho.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.initialOffset
import org.xtimms.tokusho.core.motion.materialSharedAxisXIn
import org.xtimms.tokusho.core.motion.materialSharedAxisXOut
import org.xtimms.tokusho.sections.explore.EXPLORE_DESTINATION
import org.xtimms.tokusho.sections.history.HISTORY_DESTINATION
import org.xtimms.tokusho.sections.search.SEARCH_DESTINATION
import org.xtimms.tokusho.sections.settings.SETTINGS_DESTINATION
import org.xtimms.tokusho.sections.shelf.SHELF_DESTINATION
import org.xtimms.tokusho.ui.theme.TokushoTheme

@Composable
fun TopAppBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isVisible by remember {
        derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                SHELF_DESTINATION, HISTORY_DESTINATION, EXPLORE_DESTINATION,
                null -> true

                else -> false
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = materialSharedAxisXIn(initialOffsetX = { -(it * initialOffset).toInt() }),
        exit = materialSharedAxisXOut(targetOffsetX = { -(it * initialOffset).toInt() })
    ) {
        Row(
            modifier = modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Card(
                onClick = { navController.navigate(SEARCH_DESTINATION) },
                modifier = modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                ),
            ) {
                Row(
                    modifier = modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = stringResource(R.string.search),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = modifier,
            ) {
                IconButton(
                    onClick = {  },
                    modifier = modifier.padding(0.dp),
                ) {
                    Icon(
                        Icons.Outlined.RssFeed,
                        contentDescription = stringResource(id = R.string.feed),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = { navController.navigate(SETTINGS_DESTINATION) },
                    modifier = modifier.padding(0.dp),
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit,
) {
    LargeTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit,
) {
    MediumTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassicTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit,
) {
    androidx.compose.material3.TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DefaultTopAppBarPreview() {
    TokushoTheme {
        DefaultTopAppBar(
            title = "Tokusho",
            navigateBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SmallTopAppBarPreview() {
    TokushoTheme {
        SmallTopAppBar(
            title = "Tokusho",
            navigateBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ClassicTopAppBarPreview() {
    TokushoTheme {
        ClassicTopAppBar(
            title = "Tokusho",
            navigateBack = {}
        )
    }
}