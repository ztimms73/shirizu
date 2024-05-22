package org.xtimms.shirizu.sections.library.shelves

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ShirizuAsyncImage

@Composable
fun ShelfItem(
    onClick: () -> Unit,
    categoryTitle: String,
    numberOfFavourites: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .height(IntrinsicSize.Max)
                .padding(
                    PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box {
                Card(
                    modifier = Modifier
                        .height(64.dp)
                        .width(48.dp)
                        .absoluteOffset(
                            y = 8.dp
                        ),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    content = {}
                )
                Card(
                    modifier = Modifier
                        .aspectRatio(2 / 3f)
                        .width(48.dp),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ) {
                    ShirizuAsyncImage(
                        model = "https://avatars.githubusercontent.com/u/61558546?v=4",
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = categoryTitle)
                Text(text = pluralStringResource(
                    id = R.plurals.mangas,
                    numberOfFavourites,
                    numberOfFavourites
                ), style = MaterialTheme.typography.bodySmall)
            }
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
    }
}
