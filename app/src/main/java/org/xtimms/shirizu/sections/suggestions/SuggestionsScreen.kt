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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.MangaGridItem
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.components.icons.Creation
import org.xtimms.shirizu.core.model.parcelable.ParcelableManga
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.sections.details.DetailsScreen
import org.xtimms.shirizu.utils.lang.Screen

object SuggestionsScreen : Screen() {

    private val snackbarHostState = SnackbarHostState()

    @Composable
    override fun Content() {

        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SuggestionsScreenModel>()
        val state by screenModel.state.collectAsState()

        ScaffoldWithTopAppBar(
            title = stringResource(id = R.string.suggestions),
            navigateBack = navigator::pop,
            actions = {
                IconButton(onClick = { screenModel.updateSuggestions() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(id = R.string.refresh)
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            state.list.let {
                if (it == null) {
                    LoadingScreen(Modifier.padding(padding))
                } else if (it.isEmpty()) {
                    EmptyScreen(
                        icon = Icons.Outlined.Creation,
                        title = R.string.nothing_here,
                        description = R.string.empty_suggestions_hint
                    )
                } else {
                    SuggestionsScreenContent(
                        suggestions = it,
                        contentPadding = padding,
                        onClick = { suggestion -> navigator.push(DetailsScreen(suggestion.manga.id)) }
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.events.collectLatest { e ->
                when (e) {
                    SuggestionsScreenModel.Event.GettingSuggestions -> {
                        state.isLoading = true
                        snackbarHostState.showSnackbar(context.resources.getString(R.string.suggestions_updating))
                    }
                    SuggestionsScreenModel.Event.InternalError ->
                        snackbarHostState.showSnackbar(context.resources.getString(R.string.error_occured))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SuggestionsScreenContent(
    suggestions: List<SuggestionMangaModel>,
    contentPadding: PaddingValues,
    onClick: (SuggestionMangaModel) -> Unit,
) {
    val listState = rememberLazyGridState()
    Box(
        modifier = Modifier
            .padding(contentPadding),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    MangaGridItem(
                        manga = item.manga,
                        title = item.manga.title,
                        onClick = { onClick(item) },
                        onLongClick = { },
                    )
                }
            }
        }
    }
}