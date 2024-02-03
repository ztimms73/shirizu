package org.xtimms.tokusho.sections.shelf

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.xtimms.tokusho.core.collapsable
import org.xtimms.tokusho.core.model.ShelfCategory
import org.xtimms.tokusho.ui.theme.TokushoTheme

const val SHELF_DESTINATION = "stub"

@Composable
fun ShelfView(
    categories: List<ShelfCategory>,
    currentPage: () -> Int,
    showPageTabs: Boolean,
    getNumberOfMangaForCategory: (ShelfCategory) -> Int?,
    getLibraryForPage: (Int) -> List<ShelfItem>,
    topBarHeightPx: Float,
    padding: PaddingValues,
) {
    ShelfViewContent(
        categories = categories,
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
    categories: List<ShelfCategory>,
    currentPage: () -> Int,
    showPageTabs: Boolean,
    getNumberOfMangaForCategory: (ShelfCategory) -> Int?,
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
        val coercedCurrentPage = remember { currentPage().coerceAtMost(categories.lastIndex) }
        val pagerState = rememberPagerState(coercedCurrentPage) { categories.size }
        val scope = rememberCoroutineScope()
        if (showPageTabs && categories.size > 1) {
            LaunchedEffect(categories) {
                if (categories.size <= pagerState.currentPage) {
                    pagerState.scrollToPage(categories.size - 1)
                }
            }
            ShelfTabs(
                categories = categories,
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

@Preview
@Composable
fun ShelfPreview() {
    val library: ShelfMap = emptyMap()
    TokushoTheme {
        Surface {
            ShelfViewContent(
                categories = emptyList(),
                currentPage = { 2 },
                showPageTabs = true,
                getNumberOfMangaForCategory = { 2 },
                getLibraryForPage = { library.values.toTypedArray().getOrNull(0).orEmpty() },
                padding = PaddingValues(),
                topBarHeightPx = 0f,
            )
        }
    }
}

typealias ShelfMap = Map<ShelfCategory, List<ShelfItem>>