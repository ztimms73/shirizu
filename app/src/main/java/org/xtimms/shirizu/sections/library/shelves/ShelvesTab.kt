package org.xtimms.shirizu.sections.library.shelves

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

@Composable
fun Screen.shelvesTab(): TabContent {

    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current
    val screenModel = getScreenModel<ShelvesScreenModel>()
    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = R.string.shelves,
        content = { contentPadding, snackbarHostState ->
            ShelvesScreen(
                state = state,
                contentPadding = contentPadding
            )

            LaunchedEffect(Unit) {
                screenModel.events.collectLatest { event ->
                    when (event) {
                        ShelvesScreenModel.Event.InternalError -> {
                            launch { snackbarHostState.showSnackbar(context.resources.getString(R.string.error_occured)) }
                        }
                    }
                }
            }
        }
    )

}