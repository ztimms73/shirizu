package org.xtimms.shirizu.sections.shelf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.utils.system.plus

@Composable
fun ShelfPager(
    state: PagerState,
    contentPadding: PaddingValues,
    selectedManga: List<ShelfManga>,
    getShelfForPage: (Int) -> List<ShelfItem>,
    onClickManga: (Manga) -> Unit,
    onLongClickManga: (Manga) -> Unit,
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
        val library = getShelfForPage(page)
        if (library.isEmpty()) {
            ShelfPagerEmptyScreen(
                contentPadding = contentPadding,
            )
            return@HorizontalPager
        }

        ShelfGrid(
            items = library,
            columns = AppSettings.getGridColumnsCount().toInt(),
            contentPadding = contentPadding,
            selection = selectedManga,
            onClick = onClickManga,
            onLongClick = onLongClickManga,
        )
    }
}

@Composable
private fun ShelfPagerEmptyScreen(
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .padding(contentPadding + PaddingValues(8.dp))
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        EmptyScreen(
            icon = Icons.Outlined.Close,
            title = R.string.empty_here,
            description = R.string.information_no_manga_category,
            modifier = Modifier.weight(1f),
        )
    }
}