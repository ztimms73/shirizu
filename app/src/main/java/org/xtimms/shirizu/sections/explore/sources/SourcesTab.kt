package org.xtimms.shirizu.sections.explore.sources

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
import org.xtimms.shirizu.sections.list.MangaListScreen

@Composable
fun Screen.sourcesTab(): TabContent {

    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current
    val screenModel = getScreenModel<SourcesScreenModel>()
    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = R.string.sources,
        content = { contentPadding, snackbarHostState ->
            SourcesScreen(
                state = state,
                contentPadding = contentPadding,
                onClickItem = { source ->
                    navigator.push(MangaListScreen(source))
                },
                onClickMenu = { },
                onClickHide = { screenModel.hideSource(it) },
                onLongClickItem = { },
            )

            LaunchedEffect(Unit) {
                screenModel.events.collectLatest { event ->
                    when (event) {
                        SourcesScreenModel.Event.InternalError -> {
                            launch { snackbarHostState.showSnackbar(context.resources.getString(R.string.error_occured)) }
                        }
                    }
                }
            }
        },
    )
}