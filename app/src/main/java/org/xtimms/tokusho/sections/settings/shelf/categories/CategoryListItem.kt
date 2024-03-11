package org.xtimms.tokusho.sections.settings.shelf.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.xtimms.tokusho.R
import org.xtimms.tokusho.sections.shelf.FavouriteTabModel

@Composable
fun CategoryListItem(
    category: FavouriteTabModel,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: (FavouriteTabModel) -> Unit,
    onMoveDown: (FavouriteTabModel) -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRename() }
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.Label, contentDescription = null)
            Text(
                text = category.title,
                modifier = Modifier
                    .padding(start = 16.dp),
            )
        }
        Row {
            IconButton(
                onClick = { onMoveUp(category) },
                enabled = canMoveUp,
            ) {
                Icon(imageVector = Icons.Outlined.ArrowDropUp, contentDescription = null)
            }
            IconButton(
                onClick = { onMoveDown(category) },
                enabled = canMoveDown,
            ) {
                Icon(imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onRename) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.action_rename_category),
                )
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = stringResource(R.string.action_delete))
            }
        }
    }
}