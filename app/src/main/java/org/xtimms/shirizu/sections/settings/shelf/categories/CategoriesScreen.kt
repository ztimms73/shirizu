package org.xtimms.shirizu.sections.settings.shelf.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.ScaffoldWithClassicTopAppBar
import org.xtimms.shirizu.core.model.FavouriteCategory
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.utils.system.plus

@Composable
fun CategoriesScreen(
    state: CategoryScreenState.Success,
    onClickCreate: () -> Unit,
    onClickRename: (FavouriteCategory) -> Unit,
    onClickDelete: (FavouriteCategory) -> Unit,
    onReorder: (List<FavouriteCategory>) -> Unit,
    navigateUp: () -> Unit,
) {

    val lazyListState = rememberLazyListState()

    ScaffoldWithClassicTopAppBar(
        title = stringResource(R.string.edit_categories),
        floatingActionButton = {
            CategoryFloatingActionButton(onCreate = { onClickCreate() })
        },
        navigateBack = navigateUp
    ) { paddingValues ->
        if (state.isEmpty) {
            EmptyScreen(
                icon = Icons.Outlined.Category,
                title = R.string.information_empty_category,
                description = R.string.on,
                modifier = Modifier.padding(paddingValues),
            )
            return@ScaffoldWithClassicTopAppBar
        }

        CategoryContent(
            categories = state.categories,
            lazyListState = lazyListState,
            paddingValues = paddingValues +
                    PaddingValues(top = 8.dp) +
                    PaddingValues(horizontal = 8.dp),
            onClickRename = onClickRename,
            onClickDelete = onClickDelete,
            onReorder = onReorder,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryContent(
    categories: List<FavouriteCategory>,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    onClickRename: (FavouriteCategory) -> Unit,
    onClickDelete: (FavouriteCategory) -> Unit,
    onReorder: (List<FavouriteCategory>) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = categories,
            key = { _, category -> "category-${category.id}" },
        ) { index, category ->
            CategoryListItem(
                modifier = Modifier.animateItem(),
                category = category,
                canMoveUp = index != 0,
                canMoveDown = index != categories.lastIndex,
                onReorder = onReorder,
                onRename = { onClickRename(category) },
                onDelete = { onClickDelete(category) },
            )
        }
    }
}

@Composable
fun CategoryFloatingActionButton(
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        text = { Text(text = stringResource(R.string.add_category)) },
        icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) },
        onClick = onCreate,
        modifier = modifier,
    )
}