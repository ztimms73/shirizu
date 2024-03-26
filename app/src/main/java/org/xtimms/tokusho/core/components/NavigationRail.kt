package org.xtimms.tokusho.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import org.xtimms.tokusho.core.BottomNavDestination
import org.xtimms.tokusho.core.BottomNavDestination.Companion.Icon
import org.xtimms.tokusho.sections.search.SEARCH_DESTINATION

@Composable
fun NavigationRail(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    NavigationRail(
        header = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(SEARCH_DESTINATION) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomNavDestination.railValues.forEachIndexed { index, dest ->
                val isSelected = navBackStackEntry?.destination?.route == dest.route
                NavigationRailItem(
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