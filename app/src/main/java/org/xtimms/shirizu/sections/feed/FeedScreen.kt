package org.xtimms.shirizu.sections.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.ConfirmButton
import org.xtimms.shirizu.core.components.DialogCheckBoxItem
import org.xtimms.shirizu.core.components.DismissButton
import org.xtimms.shirizu.core.components.ListGroupHeader
import org.xtimms.shirizu.core.components.ScaffoldWithClassicTopAppBar
import org.xtimms.shirizu.core.components.ShirizuDialog
import org.xtimms.shirizu.core.components.effects.RowEntity
import org.xtimms.shirizu.core.components.effects.RowEntityType
import org.xtimms.shirizu.core.components.effects.animatedItemsIndexed
import org.xtimms.shirizu.core.components.effects.updateAnimatedItemsState
import org.xtimms.shirizu.core.tracker.model.TrackingLogItem
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.sections.feed.model.toFeedItem
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.calculateTimeAgo
import org.xtimms.shirizu.utils.lang.isSameDay
import java.time.Instant

@OptIn(ExperimentalFoundationApi::class)
object FeedScreen : Screen() {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        var showClearDialog by remember { mutableStateOf(false) }

        val screenModel = getScreenModel<FeedScreenModel>()
        val state by screenModel.state.collectAsState()

        val animatedList = run {
            val list = emptyList<RowEntity>().toMutableList()
            var createdAt: Instant? = null
            state.list?.forEach { item ->

                if (createdAt === null || !isSameDay(
                        item.createdAt.toEpochMilli(),
                        createdAt!!.toEpochMilli()
                    )
                ) {
                    createdAt = item.createdAt

                    list.add(
                        RowEntity(
                            type = RowEntityType.Header,
                            key = "header-${createdAt}",
                            itemModel = null,
                            day = createdAt!!,
                        )
                    )
                }
                list.add(
                    RowEntity(
                        type = RowEntityType.Item,
                        key = "item-${item.manga.id}-${item.createdAt}",
                        day = createdAt!!,
                        itemModel = item
                    )
                )
            }
            updateAnimatedItemsState(newList = list.toList().map { it })
        }

        ScaffoldWithClassicTopAppBar(
            title = stringResource(R.string.feed),
            navigateBack = navigator::pop,
            actions = {
                IconButton(onClick = { screenModel.updateFeed() }) {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                }
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Outlined.Tune, contentDescription = null)
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = { showClearDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ClearAll,
                        contentDescription = "Clear all"
                    )
                    Text(
                        text = stringResource(R.string.clear_all),
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                }
            }
        ) { padding ->
            state.list.let {
                if (it == null) {
                    LoadingScreen(Modifier.padding(padding))
                } else if (it.isEmpty()) {
                    EmptyScreen(
                        icon = Icons.Outlined.History,
                        title = R.string.empty_history_title,
                        description = R.string.empty_history_description
                    )
                } else {
                    Box(
                        Modifier.fillMaxSize()
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = padding
                            ) {
                                animatedItemsIndexed(
                                    state = animatedList.value,
                                    key = { rowItem -> rowItem.key },
                                ) { _, item ->
                                    when (item.type) {
                                        RowEntityType.Header -> ListGroupHeader(
                                            calculateTimeAgo(item.day).format(
                                                LocalContext.current.resources
                                            )
                                        )

                                        RowEntityType.Item -> FeedViewItem(
                                            modifier = Modifier.animateItem(),
                                            selected = false,
                                            feed = (item.itemModel as TrackingLogItem).toFeedItem(),
                                            onClick = { /*TODO*/ },
                                            onLongClick = { /*TODO*/ }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showClearDialog) {
            ClearFeedDialog(
                onDismissRequest = { showClearDialog = false },
                isClearInfoAboutNewChaptersSelected = false,
                onConfirm = { isClearInfoAboutNewChaptersSelected ->
                    if (isClearInfoAboutNewChaptersSelected) {
                        screenModel.clearFeed(true)
                    } else {
                        screenModel.clearFeed(false)
                    }
                }
            )
        }
    }
}

@Composable
fun ClearFeedDialog(
    onDismissRequest: () -> Unit = {},
    isClearInfoAboutNewChaptersSelected: Boolean,
    onConfirm: (isPagesCacheSelected: Boolean) -> Unit = { _ -> }
) {

    var infoAboutNewChapters by remember {
        mutableStateOf(isClearInfoAboutNewChaptersSelected)
    }

    ShirizuDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ConfirmButton {
                onConfirm(infoAboutNewChapters)
                onDismissRequest()
            }
        },
        dismissButton = {
            DismissButton {
                onDismissRequest()
            }
        },
        title = {
            Text(
                text = stringResource(
                    id = R.string.clear_updates_feed
                )
            )
        },
        icon = { Icon(imageVector = Icons.Outlined.ClearAll, contentDescription = null) },
        text = {
            Column {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.clear_updates_feed_desc)
                )
                DialogCheckBoxItem(
                    text = stringResource(id = R.string.clear_info_about_new_chapters),
                    checked = infoAboutNewChapters
                ) {
                    infoAboutNewChapters = !infoAboutNewChapters
                }
            }
        })
}

@Preview
@Composable
private fun ClearFeedDialogPreview() {
    ClearFeedDialog(
        onDismissRequest = {},
        isClearInfoAboutNewChaptersSelected = false
    )
}