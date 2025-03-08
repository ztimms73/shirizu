package org.xtimms.shirizu.sections.library.shelves

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.CoverPlaceholderColor
import org.xtimms.shirizu.core.components.MangaCover
import org.xtimms.shirizu.core.model.Cover

private data class CoverConfiguration(
    val offsetX: Dp,
    val offsetY: Dp,
    val scale: Float,
    val darken: Float
)

private val coverConfigurations = listOf(
    CoverConfiguration(
        offsetX = 0.dp,
        offsetY = 0.dp,
        scale = 1f,
        darken = 0f
    ),
    CoverConfiguration(
        offsetX = 12.dp,
        offsetY = 0.dp,
        scale = 0.9f,
        darken = 0.3f
    ),
    CoverConfiguration(
        offsetX = 24.dp,
        offsetY = 0.dp,
        scale = 0.8f,
        darken = 0.6f
    )
)

@Composable
fun ShelfItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    categoryTitle: String,
    numberOfFavourites: Int,
    covers: List<Cover> = emptyList(),
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
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            val maxSlots = 3
            val coversToDisplay = covers.take(maxSlots)
            val coverSlots = List(maxSlots) { index ->
                coversToDisplay.getOrNull(index)
            }
            Box {
                for (i in coverConfigurations.indices.reversed()) {
                    val config = coverConfigurations[i]
                    val cover = coverSlots[i]
                    val baseWidth = 48.dp
                    val width = baseWidth * config.scale
                    Card(
                        modifier = Modifier
                            .absoluteOffset(x = config.offsetX, y = config.offsetY)
                            .aspectRatio(2 / 3f)
                            .width(width),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (cover != null) {
                                MangaCover.Book(
                                    data = cover.url,
                                    shape = RoundedCornerShape(0.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(CoverPlaceholderColor)
                                )
                            }
                            if (config.darken > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Color.Black.copy(alpha = config.darken)
                                        )
                                )
                            }
                        }
                    }
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