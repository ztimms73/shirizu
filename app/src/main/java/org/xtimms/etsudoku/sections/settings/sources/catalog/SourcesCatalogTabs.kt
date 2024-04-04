package org.xtimms.etsudoku.sections.settings.sources.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.koitharu.kotatsu.parsers.model.ContentType
import org.koitharu.kotatsu.parsers.model.MangaState
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.TabText

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun SourcesCatalogTabs(
    categories: List<SourceCatalogPage>,
    pagerState: PagerState,
    onTabItemClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.zIndex(1f),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            // TODO: use default when width is fixed upstream
            // https://issuetracker.google.com/issues/242879624
            divider = {},
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { onTabItemClick(index) },
                    text = {
                        TabText(
                            text = when (category.type) {
                                ContentType.MANGA -> stringResource(id = R.string.manga)
                                ContentType.COMICS -> stringResource(id = R.string.comics)
                                ContentType.HENTAI -> stringResource(id = R.string.hentai)
                                ContentType.OTHER -> stringResource(id = R.string.other)
                                else -> stringResource(id = R.string.unknown)
                            },
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        HorizontalDivider()
    }
}