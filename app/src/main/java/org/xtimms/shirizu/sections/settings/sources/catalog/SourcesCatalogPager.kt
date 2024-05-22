package org.xtimms.shirizu.sections.settings.sources.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.utils.system.plus

@Composable
fun SourcesCatalogPager(
    coil: ImageLoader,
    state: PagerState,
    contentPadding: PaddingValues,
    searchQuery: String?,
    getSourcesForPage: (Int) -> List<SourceCatalogPage>,
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

        val sources = getSourcesForPage(page)

        if (sources.isEmpty()) {
            SourcesCatalogPagerEmptyScreen(
                searchQuery = searchQuery,
                contentPadding = contentPadding,
            )
            return@HorizontalPager
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            items(
                items = sources,
            ) { item ->
                item.items.forEach { source ->
                    SourceCatalogItem(
                        source = source.source,
                    )
                }
            }
        }
    }
}

@Composable
private fun SourcesCatalogPagerEmptyScreen(
    searchQuery: String?,
    contentPadding: PaddingValues,
) {
    val msg = when {
        !searchQuery.isNullOrEmpty() -> R.string.no_results_found
        else -> R.string.information_no_manga_category
    }

    Column(
        modifier = Modifier
            .padding(contentPadding + PaddingValues(8.dp))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EmptyScreen(
            icon = Icons.Outlined.Close,
            title = R.string.empty_here,
            description = msg,
            modifier = Modifier.weight(1f),
        )
    }
}