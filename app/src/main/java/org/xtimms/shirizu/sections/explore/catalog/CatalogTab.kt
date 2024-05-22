package org.xtimms.shirizu.sections.explore.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.TabContent
import org.xtimms.shirizu.sections.explore.sources.SourcesScreen
import org.xtimms.shirizu.sections.explore.sources.SourcesScreenModel
import org.xtimms.shirizu.sections.list.MangaListScreen

@Composable
fun Screen.catalogTab(): TabContent {

    val context = LocalContext.current
    val screenModel = getScreenModel<CatalogScreenModel>()
    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = R.string.catalog,
        content = { contentPadding, snackbarHostState ->
            CatalogScreen(
                state = state,
                contentPadding = contentPadding,
                onClickItem = { source ->

                },
                onClickMenu = { },
                onClickEnable = { screenModel.enableSource(it) },
                onLongClickItem = { },
                onFilterChanged = { screenModel.search(it) },
                onToggleEnableMangaSources = { screenModel.filterMangaSources(it) },
                onToggleEnableHentaiSources = { screenModel.filterHentaiSources(it) },
                onToggleEnableComicsSources = { screenModel.filterComicsSources(it) },
                onToggleEnableOtherSources = { screenModel.filterOtherSources(it) }
            )

            LaunchedEffect(Unit) {
                screenModel.events.collectLatest { event ->
                    when (event) {
                        CatalogScreenModel.Event.InternalError -> {
                            launch { snackbarHostState.showSnackbar(context.resources.getString(R.string.error_occured)) }
                        }
                    }
                }
            }
        },
    )
}