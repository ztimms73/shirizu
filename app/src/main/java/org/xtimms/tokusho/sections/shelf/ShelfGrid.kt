package org.xtimms.tokusho.sections.shelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastAny
import coil.ImageLoader
import org.xtimms.tokusho.core.components.MangaGridItem

@Composable
internal fun ShelfGrid(
    coil: ImageLoader,
    items: List<ShelfManga>,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<ShelfManga>,
    onClick: (ShelfManga) -> Unit,
    onLongClick: (ShelfManga) -> Unit,
) {
    LazyShelfGrid(
        modifier = Modifier.fillMaxSize(),
        columns = columns,
        contentPadding = contentPadding,
    ) {
        items(
            items = items,
            contentType = { "shelf_grid_item" },
        ) { shelfItem ->
            val manga = shelfItem.manga
            MangaGridItem(
                coil = coil,
                manga = manga,
                isSelected = selection.fastAny { it.id == shelfItem.id },
                onLongClick = { onLongClick(shelfItem) },
                onClick = { onClick(shelfItem) },
            )
        }
    }
}