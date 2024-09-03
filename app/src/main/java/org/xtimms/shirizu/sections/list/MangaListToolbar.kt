package org.xtimms.shirizu.sections.list

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.persistentListOf
import org.koitharu.kotatsu.parsers.model.MangaParserSource
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.AppBar
import org.xtimms.shirizu.core.components.AppBarActions
import org.xtimms.shirizu.core.components.AppBarTitle
import org.xtimms.shirizu.core.components.SearchToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSourceToolbar(
    searchQuery: String?,
    onSearchQueryChange: (String?) -> Unit,
    source: MangaParserSource?,
    navigateUp: () -> Unit,
    onWebViewClick: () -> Unit,
    onSearch: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    // Avoid capturing unstable source in actions lambda
    val title = source?.title

    SearchToolbar(
        navigateUp = navigateUp,
        titleContent = { AppBarTitle(title) },
        searchQuery = searchQuery,
        onChangeSearchQuery = onSearchQueryChange,
        onSearch = onSearch,
        onClickCloseSearch = navigateUp,
        actions = {
            AppBarActions(
                actions = persistentListOf<AppBar.AppBarAction>().builder()
                    .apply {
                        add(
                            AppBar.OverflowAction(
                                title = stringResource(R.string.open_in_browser),
                                onClick = onWebViewClick,
                            ),
                        )
                    }
                    .build(),
            )
        },
        scrollBehavior = scrollBehavior,
    )
}