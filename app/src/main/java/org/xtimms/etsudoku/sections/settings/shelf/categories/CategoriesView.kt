package org.xtimms.etsudoku.sections.settings.shelf.categories

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.ScaffoldWithClassicTopAppBar
import org.xtimms.etsudoku.sections.shelf.ShelfViewModel
import org.xtimms.etsudoku.utils.system.plus

const val CATEGORIES_DESTINATION = "categories"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesView(
    shelfViewModel: ShelfViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {

    val categories by shelfViewModel.categories.collectAsStateWithLifecycle(emptyList())
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    ScaffoldWithClassicTopAppBar(
        title = stringResource(R.string.edit_categories),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    showAddCategoryDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.NewLabel,
                    contentDescription = "New category"
                )
                Text(
                    text = stringResource(R.string.add),
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                )
            }
        },
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = padding + PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(
                items = categories,
                key = { _, category -> "category-${category.id}" },
            ) { index, category ->
                CategoryListItem(
                    modifier = Modifier.animateItemPlacement(),
                    category = category,
                    canMoveUp = index != 0,
                    canMoveDown = index != categories.lastIndex,
                    onMoveUp = { },
                    onMoveDown = { },
                    onRename = { },
                    onDelete = { },
                )
            }
        }
    }
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismissRequest = { showAddCategoryDialog = false }
        )
    }

}