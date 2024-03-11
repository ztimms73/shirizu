package org.xtimms.tokusho.sections.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.tokusho.core.components.MangaGridItem
import org.xtimms.tokusho.core.components.ScaffoldWithSmallTopAppBarWithChips
import org.xtimms.tokusho.utils.composable.onBottomReached
import org.xtimms.tokusho.utils.system.toast

const val PROVIDER_ARGUMENT = "{source}"
const val LIST_DESTINATION = "provider/${PROVIDER_ARGUMENT}"

@Composable
fun MangaListView(
    coil: ImageLoader,
    source: MangaSource,
    navigateBack: () -> Unit,
    navigateToDetails: (Long) -> Unit,
) {
    val viewModel: MangaListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MangaListViewContent(
        coil = coil,
        source = source,
        uiState = uiState,
        event = viewModel,
        navigateBack = navigateBack,
        navigateToDetails = navigateToDetails
    )
}

@Composable
private fun MangaListViewContent(
    coil: ImageLoader,
    source: MangaSource,
    uiState: MangaListUiState,
    event: MangaListEvent?,
    navigateBack: () -> Unit,
    navigateToDetails: (Long) -> Unit,
) {
    val context = LocalContext.current

    if (uiState.message != null) {
        LaunchedEffect(uiState.message) {
            context.toast(uiState.message)
            event?.onMessageDisplayed()
        }
    }

    ScaffoldWithSmallTopAppBarWithChips(
        title = source.title,
        chips = listOf(
            "Chip 1",
            "Chip 2",
            "Chip 3",
            "Chip 4",
            "Chip 1",
            "Chip 2",
            "Chip 3",
            "Chip 4"
        ),
        navigateBack = navigateBack,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        val listState = rememberLazyGridState()
        listState.onBottomReached(buffer = 5) {
            event?.loadMore()
        }
        Box(
            modifier = Modifier
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = uiState.isLoading,
                exit = fadeOut(),
            ) {
                CircularProgressIndicator()
            }
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = slideInVertically(tween(500)) { 64 } + fadeIn(),
                exit = fadeOut()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    state = listState,
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        top = 8.dp,
                        end = 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    items(
                        items = uiState.manga,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            MangaGridItem(
                                coil = coil,
                                manga = item,
                                onClick = {
                                    navigateToDetails(item.id)
                                },
                                onLongClick = { },
                            )
                        }
                    }
                }
            }
        }
    }
}