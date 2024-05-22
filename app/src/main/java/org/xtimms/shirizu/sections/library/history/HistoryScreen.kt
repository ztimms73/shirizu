package org.xtimms.shirizu.sections.library.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.FastScrollLazyColumn
import org.xtimms.shirizu.core.components.FilterSortPanel
import org.xtimms.shirizu.core.components.ListGroupHeader
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.SearchTextField
import org.xtimms.shirizu.core.components.SortChip
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.NSFW_HISTORY
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.utils.lang.calculateTimeAgo
import org.xtimms.shirizu.utils.system.plus
import java.time.Instant

@Composable
fun HistoryScreen(
    state: HistoryScreenModel.State,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onToggleEnableNsfw: (Boolean) -> Unit,
    onFilterChanged: (String) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onClick: (HistoryItemModel) -> Unit,
    onHistorySelected: (HistoryItemModel, Boolean, Boolean, Boolean) -> Unit,
) {
    when {
        state.isLoading -> LoadingScreen(
            Modifier.padding(contentPadding)
        )
        /*state.isEmpty -> EmptyScreen(
            icon = Icons.Outlined.History,
            title = R.string.empty_history_title,
            description = R.string.empty_history_description,
            modifier = Modifier.padding(contentPadding),
        )*/

        else -> {
            HistoryScreenContent(
                state = state,
                snackbarHostState = snackbarHostState,
                contentPadding = contentPadding + PaddingValues(top = 8.dp),
                onToggleEnableNsfw = onToggleEnableNsfw,
                onFilterChanged = onFilterChanged,
                onSortSelected = onSortSelected,
                onClick = onClick,
                onHistorySelected = onHistorySelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryScreenContent(
    state: HistoryScreenModel.State,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onToggleEnableNsfw: (Boolean) -> Unit,
    onFilterChanged: (String) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onClick: (HistoryItemModel) -> Unit,
    onHistorySelected: (HistoryItemModel, Boolean, Boolean, Boolean) -> Unit,
) {

    var filterExpanded by remember { mutableStateOf(false) }
    var isNsfwInHistoryEnabled by remember { mutableStateOf(AppSettings.showNsfwInHistory()) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) {
        FastScrollLazyColumn(
            contentPadding = contentPadding,
        ) {
            item {
                var filter by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(TextFieldValue(""))
                }
                FilterSortPanel(
                    filterIcon = {
                        IconButton(onClick = { filterExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null, // FIXME
                            )
                        }
                    },
                    filterTextField = {
                        SearchTextField(
                            value = filter,
                            onValueChange = { value ->
                                filter = value
                                onFilterChanged(value.text)
                            },
                            hint = stringResource(id = R.string.search_by_reading_history),
                            modifier = Modifier.fillMaxWidth(),
                            onCleared = {
                                filter = TextFieldValue()
                                onFilterChanged("")
                                filterExpanded = false
                            },
                        )
                    },
                    filterExpanded = filterExpanded,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    FilterChip(
                        selected = isNsfwInHistoryEnabled,
                        leadingIcon = {
                            AnimatedVisibility(visible = isNsfwInHistoryEnabled) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                )
                            }
                        },
                        onClick = {
                            isNsfwInHistoryEnabled = !isNsfwInHistoryEnabled
                            AppSettings.updateValue(NSFW_HISTORY, isNsfwInHistoryEnabled)
                            onToggleEnableNsfw(isNsfwInHistoryEnabled)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.show_nsfw))
                        },
                    )

                    SortChip(
                        sortOptions = state.availableSorts,
                        currentSortOption = state.sort,
                        onSortSelected = onSortSelected,
                    )
                }
            }

            historyUiItems(
                uiModels = state.getUiModel(),
                selectionMode = state.selectionMode,
                onHistorySelected = onHistorySelected,
                onClickHistory = onClick,
            )
        }
    }
}

internal fun LazyListScope.historyUiItems(
    uiModels: List<HistoryUiModel>,
    selectionMode: Boolean,
    onHistorySelected: (HistoryItemModel, Boolean, Boolean, Boolean) -> Unit,
    onClickHistory: (HistoryItemModel) -> Unit,
) {
    items(
        items = uiModels,
        contentType = {
            when (it) {
                is HistoryUiModel.Header -> "header"
                is HistoryUiModel.Item -> "item"
            }
        },
        key = {
            when (it) {
                is HistoryUiModel.Header -> "updatesHeader-${it.hashCode()}"
                is HistoryUiModel.Item -> "updates-${it.item.manga.id}"
            }
        },
    ) { item ->
        when (item) {
            is HistoryUiModel.Header -> {
                ListGroupHeader(
                    modifier = Modifier.animateItem(),
                    text = calculateTimeAgo(item.date).format(
                        LocalContext.current.resources
                    ),
                )
            }
            is HistoryUiModel.Item -> {
                val historyItem = item.item
                HistoryItem(
                    modifier = Modifier.animateItem(),
                    history = historyItem,
                    selected = historyItem.selected,
                    onLongClick = {
                        onHistorySelected(historyItem, !historyItem.selected, true, true)
                    },
                    onClick = {
                        when {
                            selectionMode -> onHistorySelected(historyItem, !historyItem.selected, true, false)
                            else -> onClickHistory(historyItem)
                        }
                    },
                )
            }
        }
    }
}

sealed interface HistoryUiModel {
    data class Header(val date: Instant) : HistoryUiModel
    data class Item(val item: HistoryItemModel) : HistoryUiModel
}