package org.xtimms.shirizu.sections.shelf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import coil.ImageLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.model.FavouriteCategory
import kotlin.time.Duration.Companion.seconds

@Composable
fun ShelfContent(
    categories: List<FavouriteCategory>,
    searchQuery: String?,
    selection: List<ShelfManga>,
    contentPadding: PaddingValues,
    currentPage: () -> Int,
    hasActiveFilters: Boolean,
    onChangeCurrentPage: (Int) -> Unit,
    onMangaClicked: (Manga) -> Unit,
    onToggleSelection: (Manga) -> Unit,
    onToggleRangeSelection: (Manga) -> Unit,
    onRefresh: (FavouriteCategory?) -> Boolean,
    onGlobalSearchClicked: () -> Unit,
    getNumberOfMangaForCategory: (FavouriteCategory) -> Int?,
    getShelfForPage: (Int) -> List<ShelfItem>,
) {
    Column(
        modifier = Modifier.padding(
            top = contentPadding.calculateTopPadding(),
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
        ),
    ) {
        val pagerState = rememberPagerState(1) { categories.size }

        val scope = rememberCoroutineScope()
        var isRefreshing by remember(pagerState.currentPage) { mutableStateOf(false) }

        if (categories.size > 1) {
            LaunchedEffect(categories) {
                if (categories.size <= pagerState.currentPage) {
                    pagerState.scrollToPage(categories.size - 1)
                }
            }
            ShelfTabs(
                categories = categories,
                pagerState = pagerState,
            ) { scope.launch { pagerState.animateScrollToPage(it) } }
        }

        val notSelectionMode = selection.isEmpty()
        val onClickManga = { manga: Manga ->
            if (notSelectionMode) {
                onMangaClicked(manga)
            } else {
                onToggleSelection(manga)
            }
        }

        ShelfPager(
            state = pagerState,
            contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
            selectedManga = selection,
            getShelfForPage = getShelfForPage,
            onClickManga = onClickManga,
            onLongClickManga = onToggleRangeSelection,
        )

        LaunchedEffect(pagerState.currentPage) {
            onChangeCurrentPage(pagerState.currentPage)
        }
    }
}