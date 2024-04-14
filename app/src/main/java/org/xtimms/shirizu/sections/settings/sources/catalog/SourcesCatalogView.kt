package org.xtimms.shirizu.sections.settings.sources.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import kotlinx.coroutines.launch
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.ScaffoldWithClassicTopAppBar

const val CATALOG_DESTINATION = "catalog"

@Composable
fun SourcesCatalogView(
    coil: ImageLoader,
    sourcesCatalogViewModel: SourcesCatalogViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {

    val categories by sourcesCatalogViewModel.content.collectAsStateWithLifecycle(emptyList())

    ScaffoldWithClassicTopAppBar(
        title = stringResource(R.string.sources_catalog),
        navigateBack = navigateBack
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            val pagerState = rememberPagerState(0) { categories.size }
            val scope = rememberCoroutineScope()
            if (categories.isNotEmpty()) {
                SourcesCatalogTabs(
                    categories = categories,
                    pagerState = pagerState,
                ) { scope.launch { pagerState.animateScrollToPage(it) } }
            }

            SourcesCatalogPager(
                coil = coil,
                state = pagerState,
                contentPadding = padding,
                searchQuery = null,
                getSourcesForPage = { categories }
            )
        }
    }
}