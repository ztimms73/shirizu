package org.xtimms.tokusho.sections.explore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.ImageLoader
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.collapsable
import org.xtimms.tokusho.core.components.ExploreButton
import org.xtimms.tokusho.core.components.SourceItem
import org.xtimms.tokusho.core.components.icons.Dice
import org.xtimms.tokusho.core.parser.favicon.faviconUri
import org.xtimms.tokusho.sections.list.LIST_DESTINATION
import org.xtimms.tokusho.utils.system.toast

const val EXPLORE_DESTINATION = "explore"

@Composable
fun ExploreView(
    coil: ImageLoader,
    navController: NavController,
    topBarHeightPx: Float,
    topBarOffsetY: Animatable<Float, AnimationVector1D>,
    padding: PaddingValues,
) {
    val viewModel: ExploreViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreViewContent(
        coil = coil,
        navController = navController,
        uiState = uiState,
        event = viewModel,
        topBarHeightPx = topBarHeightPx,
        topBarOffsetY = topBarOffsetY,
        padding = padding
    )
}

@Composable
fun ExploreViewContent(
    coil: ImageLoader,
    navController: NavController,
    uiState: ExploreUiState,
    event: ExploreEvent?,
    nestedScrollConnection: NestedScrollConnection? = null,
    topBarHeightPx: Float = 0f,
    topBarOffsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    padding: PaddingValues = PaddingValues(),
) {

    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current

    val scrollState = rememberScrollState()

    if (uiState.message != null) {
        LaunchedEffect(uiState.message) {
            context.toast(uiState.message)
            event?.onMessageDisplayed()
        }
    }

    Box(
        modifier = Modifier
            .clipToBounds()
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val listState = rememberLazyGridState()
        val listModifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopStart)
            .then(
                if (nestedScrollConnection != null)
                    Modifier.nestedScroll(nestedScrollConnection)
                else Modifier
            )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 72.dp),
            modifier = listModifier
                .collapsable(
                    state = listState,
                    topBarHeightPx = topBarHeightPx,
                    topBarOffsetY = topBarOffsetY,
                ),
            state = listState,
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(layoutDirection) + 8.dp,
                top = padding.calculateTopPadding(),
                end = padding.calculateEndPadding(layoutDirection) + 8.dp,
                bottom = padding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            item(
                span = { GridItemSpan(maxCurrentLineSpan) }
            ) {
                Row {
                    ExploreButton(
                        text = stringResource(R.string.local_storage),
                        icon = Icons.Outlined.SdStorage,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )

                    ExploreButton(
                        text = stringResource(R.string.bookmarks),
                        icon = Icons.Outlined.Bookmarks,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }
            item(
                span = { GridItemSpan(maxCurrentLineSpan) }
            ) {
                Row {
                    ExploreButton(
                        text = stringResource(R.string.random),
                        icon = Icons.Outlined.Dice,
                        modifier = Modifier.weight(1f),
                        onClick = { },
                    )

                    ExploreButton(
                        text = stringResource(R.string.downloads),
                        icon = Icons.Outlined.Download,
                        modifier = Modifier.weight(1f),
                        onClick = { throw IllegalAccessException() },
                    )
                }
            }
            items(
                items = uiState.sources,
                key = { it.ordinal },
                contentType = { it }
            ) { item ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    SourceItem(
                        coil = coil,
                        faviconUrl = item.faviconUri(),
                        title = item.title,
                        onClick = {
                            navController.navigate(LIST_DESTINATION)
                        }
                    )
                }
            }
        }
    }
}