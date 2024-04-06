package org.xtimms.shirizu.sections.suggestions

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.MangaGridItem
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar

const val SUGGESTIONS_DESTINATION = "suggestions"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionsView(
    viewModel: SuggestionsViewModel = hiltViewModel(),
    coil: ImageLoader,
    navigateBack: () -> Unit,
    navigateToDetails: (Long) -> Unit
) {

    val suggestions by viewModel.content.collectAsStateWithLifecycle(emptyList())

    ScaffoldWithTopAppBar(
        title = stringResource(id = R.string.suggestions),
        navigateBack = navigateBack,
        actions = {
            IconButton(onClick = { viewModel.updateSuggestions() }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh"
                )
            }
        },
    ) { padding ->
        val listState = rememberLazyGridState()
        Box(
            modifier = Modifier
                .padding(padding),
            contentAlignment = Alignment.Center
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
                    items = suggestions,
                    key = { it.manga.id },
                    contentType = { it }
                ) { item ->
                    Box(
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(
                            spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium / 4,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            )
                        ),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val onClickManga = { manga: Manga ->
                            navigateToDetails(manga.id)
                        }
                        MangaGridItem(
                            coil = coil,
                            manga = item.manga,
                            onClick = onClickManga,
                            onLongClick = { },
                        )
                    }
                }
            }
        }
    }
}