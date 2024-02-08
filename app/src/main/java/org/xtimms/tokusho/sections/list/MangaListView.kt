package org.xtimms.tokusho.sections.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
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
import org.xtimms.tokusho.core.components.MangaCompactGridItem
import org.xtimms.tokusho.core.components.ScaffoldWithSmallTopAppBar
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

    MangaListView(
        coil = coil,
        source = source,
        uiState = uiState,
        event = viewModel,
        navigateBack = navigateBack,
        navigateToDetails = navigateToDetails
    )
}

@Composable
private fun MangaListView(
    coil: ImageLoader,
    source: MangaSource,
    uiState: MangaListUiState,
    event: MangaListEvent?,
    navigateBack: () -> Unit,
    navigateToDetails: (Long) -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    if (uiState.message != null) {
        LaunchedEffect(uiState.message) {
            context.toast(uiState.message)
            event?.onMessageDisplayed()
        }
    }

    ScaffoldWithSmallTopAppBar(
        title = source.title,
        navigateBack = navigateBack,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        val listState = rememberLazyGridState()
        listState.onBottomReached(buffer = 3) {
            event?.loadMore()
        }
        Column(
            modifier = Modifier
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!uiState.isLoading) LazyVerticalGrid(
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
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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
                        MangaCompactGridItem(
                            coil = coil,
                            imageUrl = item.coverUrl,
                            title = item.title,
                            onClick = { navigateToDetails(item.id) },
                            onLongClick = { },
                        )
                    }
                }
            } else Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}