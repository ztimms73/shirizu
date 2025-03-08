package org.xtimms.shirizu.sections.library.shelves

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.FastScrollLazyColumn
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.core.ui.screens.LoadingScreen

@Composable
fun ShelvesScreen(
    state: ShelvesScreenModel.State,
    contentPadding: PaddingValues,
) {
    when {
        state.isLoading -> LoadingScreen(
            Modifier.padding(contentPadding)
        )

        state.isEmpty -> EmptyScreen(
            icon = Icons.Outlined.LocalLibrary,
            title = R.string.empty_history_title,
            description = R.string.empty_history_description,
            modifier = Modifier.padding(contentPadding),
        )

        else -> {
            ShelvesScreenContent(
                categories = state.categories,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun ShelvesScreenContent(
    categories: List<ShelvesScreenModel.CategoryItem>,
    contentPadding: PaddingValues,
) {
    FastScrollLazyColumn(
        contentPadding = contentPadding,
    ) {
        items(
            items = categories,
            key = { "category-${it.hashCode()}" },
        ) { item ->
            ShelfItem(
                covers = item.covers,
                modifier = Modifier.animateItem(),
                categoryTitle = item.category.title,
                numberOfFavourites = item.mangaCount,
                onClick = { },
            )
        }
    }
}