package org.xtimms.etsudoku.core

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.sections.explore.EXPLORE_DESTINATION
import org.xtimms.etsudoku.sections.history.HISTORY_DESTINATION
import org.xtimms.etsudoku.sections.shelf.SHELF_DESTINATION

sealed class BottomNavDestination(
    val value: String,
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
    val iconSelected: ImageVector,
) {
    data object Shelf : BottomNavDestination(
        value = "shelf",
        route = SHELF_DESTINATION,
        title = R.string.nav_shelf,
        icon = Icons.Outlined.LocalLibrary,
        iconSelected = Icons.Filled.LocalLibrary
    )

    data object History : BottomNavDestination(
        value = "history",
        route = HISTORY_DESTINATION,
        title = R.string.nav_history,
        icon = Icons.Outlined.History,
        iconSelected = Icons.Filled.History
    )

    data object Explore : BottomNavDestination(
        value = "explore",
        route = EXPLORE_DESTINATION,
        title = R.string.nav_explore,
        icon = Icons.Outlined.Explore,
        iconSelected = Icons.Filled.Explore
    )

    companion object {
        val values = listOf(Shelf, History, Explore)

        val railValues = listOf(Shelf, History, Explore)

        val routes = values.map { it.route }

        fun String.toBottomDestinationIndex() = when (this) {
            Shelf.value -> 0
            History.value -> 1
            Explore.value -> 2
            else -> null
        }

        @Composable
        fun BottomNavDestination.Icon(selected: Boolean) {
            androidx.compose.material3.Icon(
                imageVector = if (selected) iconSelected else icon,
                contentDescription = stringResource(title)
            )
        }
    }
}