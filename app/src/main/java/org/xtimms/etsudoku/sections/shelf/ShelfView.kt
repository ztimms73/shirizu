package org.xtimms.etsudoku.sections.shelf

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xtimms.etsudoku.core.collapsable
import org.xtimms.etsudoku.core.components.PullRefresh
import kotlin.time.Duration.Companion.seconds

const val SHELF_DESTINATION = "shelf"

@Composable
fun ShelfView(
    coil: ImageLoader,
    currentPage: () -> Int,
    showPageTabs: Boolean,
    padding: PaddingValues,
    navigateToDetails: (Long) -> Unit,
    onRefresh: (FavouriteTabModel?) -> Boolean,
) {

    ShelfViewContent(
        coil = coil,
        currentPage = currentPage,
        showPageTabs = showPageTabs,
        padding = padding,
        navigateToDetails = navigateToDetails,
        onRefresh = onRefresh,
    )
}

@Composable
fun ShelfViewContent(
    coil: ImageLoader,
    viewModel: ShelfViewModel = hiltViewModel(),
    currentPage: () -> Int,
    showPageTabs: Boolean,
    padding: PaddingValues,
    navigateToDetails: (Long) -> Unit,
    onRefresh: (FavouriteTabModel?) -> Boolean,
) {

    val categories by viewModel.categories.collectAsStateWithLifecycle(emptyList())
    val mangas by viewModel.mangas.collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = Modifier
            .padding(padding)
    ) {
        val pagerState = rememberPagerState(0) { categories.size }
        val scope = rememberCoroutineScope()

        var isRefreshing by remember(pagerState.currentPage) { mutableStateOf(false) }

        if (categories.isNotEmpty()) {
            if (showPageTabs) {
                ShelfTabs(
                    categories = categories,
                    pagerState = pagerState,
                ) { scope.launch { pagerState.animateScrollToPage(it) } }
            }
        }

        val onClickManga = { manga: ShelfManga ->
            navigateToDetails(manga.id)
        }

        PullRefresh(
            refreshing = isRefreshing,
            onRefresh = {
                val started = onRefresh(categories[currentPage()])
                if (!started) return@PullRefresh
                scope.launch {
                    // Fake refresh status but hide it after a second as it's a long running task
                    isRefreshing = true
                    delay(1.seconds)
                    isRefreshing = false
                }
            },
            enabled = { true }
        ) {
            ShelfPager(
                coil = coil,
                state = pagerState,
                contentPadding = PaddingValues(bottom = padding.calculateBottomPadding()),
                getShelfForPage = { mangas },
                navigateToDetails = onClickManga
            )
        }
    }
}