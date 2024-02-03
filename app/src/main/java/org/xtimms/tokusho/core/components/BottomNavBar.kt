package org.xtimms.tokusho.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import org.xtimms.tokusho.core.BottomNavDestination
import org.xtimms.tokusho.core.BottomNavDestination.Companion.Icon
import org.xtimms.tokusho.sections.explore.EXPLORE_DESTINATION
import org.xtimms.tokusho.sections.history.HISTORY_DESTINATION
import org.xtimms.tokusho.sections.shelf.SHELF_DESTINATION

@Composable
fun BottomNavBar(
    navController: NavController,
    bottomBarState: State<Boolean>,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isVisible by remember {
        derivedStateOf {
            when (navBackStackEntry?.destination?.route) {
                SHELF_DESTINATION, HISTORY_DESTINATION, EXPLORE_DESTINATION, null -> bottomBarState.value

                else -> false
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar {
            BottomNavDestination.values.forEachIndexed { _, dest ->
                val isSelected = navBackStackEntry?.destination?.route == dest.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        navController.navigate(dest.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { dest.Icon(selected = isSelected) },
                    label = { Text(text = stringResource(dest.title)) }
                )
            }
        }
    }
}