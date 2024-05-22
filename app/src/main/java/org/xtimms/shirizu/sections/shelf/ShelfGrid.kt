package org.xtimms.shirizu.sections.shelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastAny
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.core.components.MangaGridItem
import org.xtimms.shirizu.core.model.MangaCover

@Composable
internal fun ShelfGrid(
    items: List<ShelfItem>,
    columns: Int,
    contentPadding: PaddingValues,
    selection: List<ShelfManga>,
    onClick: (Manga) -> Unit,
    onLongClick: (Manga) -> Unit,
) {
    LazyShelfGrid(
        modifier = Modifier.fillMaxSize(),
        columns = columns,
        contentPadding = contentPadding,
    ) {
        items(
            items = items,
            contentType = { "library_comfortable_grid_item" },
        ) { libraryItem ->
            val shelfManga = libraryItem.shelfManga
            MangaGridItem(
                isSelected = selection.fastAny { it.id == libraryItem.shelfManga.id },
                title = shelfManga.manga.title,
                manga = shelfManga.manga,
                onLongClick = { onLongClick(libraryItem.shelfManga.manga) },
                onClick = { onClick(libraryItem.shelfManga.manga) },
            )
        }
    }
}