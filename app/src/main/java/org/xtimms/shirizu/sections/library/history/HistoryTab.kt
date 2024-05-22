package org.xtimms.shirizu.sections.library.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.xtimms.shirizu.MainScreen
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.DialogCheckBoxItem
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.LibraryBottomActionMenu
import org.xtimms.shirizu.core.components.ShirizuDialog
import org.xtimms.shirizu.core.ui.screens.TabContent
import org.xtimms.shirizu.sections.details.DetailsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen.historyTab(): TabContent {

    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current
    val screenModel = getScreenModel<HistoryScreenModel>()
    val state by screenModel.state.collectAsState()

    return TabContent(
        titleRes = R.string.history,
        content = { contentPadding, snackbarHostState ->

            Scaffold(
                bottomBar = {
                    LibraryBottomActionMenu(
                        visible = state.selectionMode,
                        onDeleteClicked = screenModel::openDeleteMangaDialog,
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = stringResource(R.string.continue_reading)
                        )
                        Text(
                            text = stringResource(R.string.continue_reading),
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                        )
                    }
                }
            ) {
                HistoryScreen(
                    state = state,
                    snackbarHostState = snackbarHostState,
                    contentPadding = contentPadding,
                    onToggleEnableNsfw = { screenModel.filterNsfw(it) },
                    onFilterChanged = { screenModel.search(it) },
                    onSortSelected = { screenModel.sort(it) },
                    onClick = { navigator.push(DetailsScreen(it.manga)) },
                    onHistorySelected = screenModel::toggleSelection
                )
            }

            val onDismissRequest = screenModel::closeDialog

            when (val dialog = state.dialog) {
                is HistoryScreenModel.Dialog.Delete -> run {
                    HistoryDeleteDialog(
                        onDismissRequest = onDismissRequest,
                        onDelete = {
                            screenModel.removeFromHistory(dialog.history.mapToSet { it.id })
                        },
                    )
                }

                is HistoryScreenModel.Dialog.DeleteAll -> {

                }

                null -> {}
            }

            BackHandler(enabled = state.selectionMode || state.searchQuery != null) {
                when {
                    state.selectionMode -> screenModel.clearSelection()
                    state.searchQuery != null -> screenModel.search(null)
                }
            }

            LaunchedEffect(state.selectionMode, state.dialog) {
                MainScreen.showBottomNav(!state.selectionMode)
            }

            LaunchedEffect(Unit) {
                screenModel.events.collectLatest { event ->
                    when (event) {
                        HistoryScreenModel.Event.InternalError -> {
                            launch { snackbarHostState.showSnackbar(context.resources.getString(R.string.error_occured)) }
                        }

                        HistoryScreenModel.Event.HistoryCleared -> {
                            launch { snackbarHostState.showSnackbar(context.resources.getString(R.string.history_cleared)) }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun HistoryDeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
) {
    ShirizuDialog(
        title = {
            Text(text = stringResource(R.string.action_delete))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = stringResource(R.string.delete_from_history_summary))
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDelete()
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.remove))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.dismiss))
            }
        },
    )
}