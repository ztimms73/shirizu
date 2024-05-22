package org.xtimms.shirizu.sections.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.core.components.Header
import org.xtimms.shirizu.ui.theme.ShirizuTheme

@Composable
fun SearchTabItemWithHeader(
    items: List<SearchTabItemModel>,
    title: String,
    refreshing: Boolean,
    onItemClick: (SearchTabItemModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (refreshing || items.isNotEmpty()) {
            Header(
                title = title,
                loading = refreshing,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            )
        }
        if (items.isNotEmpty()) {
            SearchTabItem(
                items = items,
                onItemClick = onItemClick,
                modifier = Modifier
                    .testTag("search_carousel")
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SearchTabItem(
    items: List<SearchTabItemModel>,
    onItemClick: (SearchTabItemModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        for (item in items) {
            Row(
                modifier
                    .height(54.dp)
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onItemClick(item)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = LocalContentColor.current
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider(modifier.padding(start = 64.dp, end = 16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTabItemPreview() {
    ShirizuTheme {
        SearchTabItemWithHeader(
            items = listOf(
                SearchTabItemModel(id = 1, icon = Icons.Outlined.Terminal, title = "Action 1"),
                SearchTabItemModel(id = 2, icon = Icons.Outlined.Terminal, title = "Action 2")
            ),
            onItemClick = { },
            refreshing = false,
            title = "Category"
        )
    }
}

data class SearchTabItemModel(
    val id: Int,
    val icon: ImageVector,
    val title: String,
)