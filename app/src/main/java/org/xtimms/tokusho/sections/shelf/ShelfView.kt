package org.xtimms.tokusho.sections.shelf

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.xtimms.tokusho.core.collapsable
import org.xtimms.tokusho.core.model.FavouriteCategory
import org.xtimms.tokusho.core.model.ShelfCategory

const val SHELF_DESTINATION = "shelf"

@Composable
fun ShelfView(
    currentPage: () -> Int,
    showPageTabs: Boolean,
    getNumberOfMangaForCategory: (FavouriteCategory) -> Int?,
    getLibraryForPage: (Int) -> List<ShelfItem>,
    topBarHeightPx: Float,
    padding: PaddingValues,
) {
    val viewModel: ShelfViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ShelfViewContent(
        uiState = uiState,
        currentPage = currentPage,
        showPageTabs = showPageTabs,
        getNumberOfMangaForCategory = getNumberOfMangaForCategory,
        getLibraryForPage = getLibraryForPage,
        topBarHeightPx = topBarHeightPx,
        padding = padding
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShelfViewContent(
    uiState: ShelfUiState,
    currentPage: () -> Int,
    showPageTabs: Boolean,
    getNumberOfMangaForCategory: (FavouriteCategory) -> Int?,
    getLibraryForPage: (Int) -> List<ShelfItem>,
    topBarHeightPx: Float,
    topBarOffsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    padding: PaddingValues,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .collapsable(
                state = scrollState,
                topBarHeightPx = topBarHeightPx,
                topBarOffsetY = topBarOffsetY
            )
            .padding(padding)
    ) {
        val coercedCurrentPage = remember { currentPage().coerceAtMost(uiState.categories.lastIndex) }
        val pagerState = rememberPagerState(coercedCurrentPage) { uiState.categories.size }
        val scope = rememberCoroutineScope()
        if (showPageTabs && uiState.categories.size > 1) {
            LaunchedEffect(uiState.categories) {
                if (uiState.categories.size <= pagerState.currentPage) {
                    pagerState.scrollToPage(uiState.categories.size - 1)
                }
            }
            ShelfTabs(
                categories = uiState.categories,
                pagerState = pagerState,
                getNumberOfMangaForCategory = getNumberOfMangaForCategory,
            ) { scope.launch { pagerState.animateScrollToPage(it) } }
        }

        ShelfPager(
            state = pagerState,
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding()),
            hasActiveFilters = false,
            searchQuery = "",
            onGlobalSearchClicked = {  },
            getLibraryForPage = getLibraryForPage,
        )
    }
}

typealias ShelfMap = Map<ShelfCategory, List<ShelfItem>>