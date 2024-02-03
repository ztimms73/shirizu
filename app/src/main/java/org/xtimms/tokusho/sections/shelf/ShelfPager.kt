package org.xtimms.tokusho.sections.shelf

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.screens.EmptyScreen
import org.xtimms.tokusho.utils.system.plus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShelfPager(
    state: PagerState,
    contentPadding: PaddingValues,
    hasActiveFilters: Boolean,
    searchQuery: String?,
    onGlobalSearchClicked: () -> Unit,
    getLibraryForPage: (Int) -> List<ShelfItem>,
) {
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = state,
        verticalAlignment = Alignment.Top,
    ) { page ->
        if (page !in ((state.currentPage - 1)..(state.currentPage + 1))) {
            // To make sure only one offscreen page is being composed
            return@HorizontalPager
        }
        val library = getLibraryForPage(page)

        if (library.isEmpty()) {
            ShelfPagerEmptyScreen(
                searchQuery = searchQuery,
                hasActiveFilters = hasActiveFilters,
                contentPadding = contentPadding,
                onGlobalSearchClicked = onGlobalSearchClicked,
            )
            return@HorizontalPager
        }

    }
}

@Composable
private fun ShelfPagerEmptyScreen(
    searchQuery: String?,
    hasActiveFilters: Boolean,
    contentPadding: PaddingValues,
    onGlobalSearchClicked: () -> Unit,
) {
    val msg = when {
        !searchQuery.isNullOrEmpty() -> R.string.no_results_found
        hasActiveFilters -> R.string.error_no_match
        else -> R.string.information_no_manga_category
    }

    Column(
        modifier = Modifier
            .padding(contentPadding + PaddingValues(8.dp))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EmptyScreen(
            title = msg,
            modifier = Modifier.weight(1f),
        )
    }
}