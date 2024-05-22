package org.xtimms.shirizu.sections.shelf

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.LibraryBottomActionMenu
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.sections.details.DetailsScreen
import org.xtimms.shirizu.utils.lang.NoLiftingAppBarScreen
import org.xtimms.shirizu.utils.lang.Tab

@OptIn(ExperimentalMaterial3Api::class)
object ShelfTab : Tab, NoLiftingAppBarScreen {

    override val options: TabOptions
        @Composable
        get() {
            val image = Icons.Outlined.LocalLibrary
            return TabOptions(
                index = 0u,
                title = stringResource(R.string.nav_shelf),
                icon = rememberVectorPainter(image),
            )
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val haptic = LocalHapticFeedback.current

        val screenModel = getScreenModel<ShelfScreenModel>()
        val state by screenModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            bottomBar = {
                LibraryBottomActionMenu(
                    visible = state.selectionMode,
                    onDeleteClicked = {}
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { contentPadding ->
            when {
                state.isLoading -> LoadingScreen(Modifier.padding(contentPadding))
                state.isShelfEmpty -> {
                    EmptyScreen(
                        icon = Icons.Outlined.Close,
                        title = R.string.empty_here,
                        description = R.string.information_no_manga_category,
                        modifier = Modifier.padding(contentPadding),
                    )
                }
                else -> {
                    ShelfContent(
                        categories = state.categories,
                        searchQuery = state.searchQuery,
                        selection = state.selection,
                        contentPadding = contentPadding,
                        currentPage = { screenModel.activeCategoryIndex },
                        hasActiveFilters = state.hasActiveFilters,
                        onChangeCurrentPage = {  },
                        onMangaClicked = { navigator.push(DetailsScreen(it)) },
                        onToggleSelection = {  },
                        onToggleRangeSelection = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onRefresh = { false },
                        onGlobalSearchClicked = {  },
                        getNumberOfMangaForCategory = { state.getMangaCountForCategory(it) },
                    ) { state.getShelfItemsByPage(it) }
                }
            }
        }
    }
}