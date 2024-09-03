package org.xtimms.shirizu.sections.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.SdCard
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.flow.collectLatest
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.MangaCarouselWithHeader
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.icons.Dice
import org.xtimms.shirizu.core.model.parcelable.ParcelableManga
import org.xtimms.shirizu.sections.details.DetailsScreen
import org.xtimms.shirizu.sections.search.global.GlobalSearchScreen
import org.xtimms.shirizu.sections.suggestions.SuggestionsScreen
import org.xtimms.shirizu.utils.composable.bodyWidth
import org.xtimms.shirizu.utils.lang.Tab

object SearchTab : Tab {

    private val snackbarHostState = SnackbarHostState()

    override val options: TabOptions
        @Composable
        get() {
            val image = Icons.Outlined.Search
            return TabOptions(
                index = 2u,
                title = stringResource(R.string.search),
                icon = rememberVectorPainter(image),
            )
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val screenModel = getScreenModel<SearchScreenModel>()
        val state by screenModel.state.collectAsState()

        val categories = listOf(
            SearchTabItemModel(
                id = 1,
                icon = Icons.Outlined.SdCard,
                title = stringResource(id = R.string.local_storage)
            ),
            SearchTabItemModel(
                id = 2,
                icon = Icons.Outlined.Bookmarks,
                title = stringResource(id = R.string.bookmarks)
            ),
            SearchTabItemModel(
                id = 3,
                icon = Icons.Outlined.Dice,
                title = stringResource(id = R.string.random)
            ),
            SearchTabItemModel(
                id = 4,
                icon = Icons.Outlined.Download,
                title = stringResource(id = R.string.downloads)
            ),
        )

        Scaffold(
            snackbarHost = { snackbarHostState }
        ) {
            LazyColumn(
                modifier = Modifier.bodyWidth(),
            ) {
                item(key = "search") {
                    Card(
                        onClick = { navigator.push(GlobalSearchScreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors()
                            .copy(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "search",
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = stringResource(R.string.search),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item(key = "carousel") {
                    MangaCarouselWithHeader(
                        items = state.list,
                        title = stringResource(id = R.string.suggestions),
                        onItemClick = { navigator.push(DetailsScreen(it.id)) },
                        onMoreClick = { navigator.push(SuggestionsScreen) },
                        refreshing = state.isLoading,
                        modifier = Modifier.animateItem(),
                    )
                }
                item(key = "categories") {
                    SearchTabItemWithHeader(
                        items = categories,
                        title = stringResource(id = R.string.categories),
                        refreshing = false,
                        onItemClick = { }
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.events.collectLatest { e ->
                when (e) {
                    SearchScreenModel.Event.GettingSuggestions -> {
                        state.isLoading = true
                        snackbarHostState.showSnackbar(context.resources.getString(R.string.suggestions_updating))
                    }
                    SearchScreenModel.Event.InternalError ->
                        snackbarHostState.showSnackbar(context.resources.getString(R.string.error_occured))
                }
            }
        }
    }
}