package org.xtimms.etsudoku.sections.shelf

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.xtimms.etsudoku.core.components.TabText
import org.xtimms.etsudoku.core.model.FavouriteCategory
import org.xtimms.etsudoku.core.prefs.AppSettings

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun ShelfTabs(
    categories: List<FavouriteTabModel>,
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
                            text = category.title,
                            badgeCount = if (AppSettings.isMangaCountInTabsEnabled()) category.mangaCount else null
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        HorizontalDivider()
    }
}