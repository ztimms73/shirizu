package org.xtimms.shirizu.sections.feed

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
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
import org.xtimms.shirizu.core.screens.EmptyScreen
import org.xtimms.shirizu.core.tracker.model.TrackingLogItem
import org.xtimms.shirizu.sections.feed.model.toFeedItem
import org.xtimms.shirizu.utils.lang.calculateTimeAgo
import org.xtimms.shirizu.utils.lang.isSameDay
import java.time.Instant

const val FEED_DESTINATION = "feed"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedView(
    coil: ImageLoader,
    viewModel: FeedViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToShelf: () -> Unit,
) {

    var showClearDialog by remember { mutableStateOf(false) }

    val feed by viewModel.content.collectAsStateWithLifecycle(emptyList())

    val animatedList = run {
        val list = emptyList<RowEntity>().toMutableList()
        var createdAt: Instant? = null
        feed.forEach { item ->

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
        navigateBack = navigateBack,
        actions = {
            IconButton(onClick = { viewModel.updateFeed() }) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
            }
            IconButton(onClick = { navigateToShelf() }) {
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
                                modifier = Modifier.animateItemPlacement(),
                                coil = coil,
                                selected = false,
                                feed = (item.itemModel as TrackingLogItem).toFeedItem(),
                                onClick = { /*TODO*/ },
                                onLongClick = { /*TODO*/ }
                            )
                        }
                    }
                }
            }
            if (feed.isEmpty()) {
                EmptyScreen(
                    icon = Icons.Outlined.RssFeed,
                    title = R.string.empty_here,
                    description = R.string.no_recent_updates
                )
            }
        }
    }

    if (showClearDialog) {
        ClearFeedDialog(
            onDismissRequest = { showClearDialog = false },
            isClearInfoAboutNewChaptersSelected = false,
            onConfirm = { isClearInfoAboutNewChaptersSelected ->
                if (isClearInfoAboutNewChaptersSelected) {
                    viewModel.clearFeed(true)
                } else {
                    viewModel.clearFeed(false)
                }
            }
        )
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