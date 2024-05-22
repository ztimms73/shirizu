package org.xtimms.shirizu.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.LocalImageLoader
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.icons.Creation
import org.xtimms.shirizu.core.ui.screens.EmptyScreen
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun MangaCarouselWithHeader(
    items: List<Manga>,
    title: String,
    refreshing: Boolean,
    onItemClick: (Manga) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (refreshing || items.isNotEmpty()) {
            Header(
                title = title,
                loading = refreshing,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ) {
                TextButton(
                    onClick = onMoreClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    modifier = Modifier.alignBy(FirstBaseline),
                ) {
                    Text(text = stringResource(id = R.string.more))
                }
            }
        }
        if (items.isNotEmpty()) {
            MangaCarousel(
                items = items,
                onItemClick = onItemClick,
                modifier = Modifier
                    .testTag("search_carousel")
                    .fillMaxWidth(),
            )
        } else {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
            ) {
                EmptyScreen(
                    icon = Icons.Outlined.Creation,
                    title = R.string.nothing_here,
                    description = R.string.empty_carousel_hint,
                    modifier = Modifier.height(IntrinsicSize.Min)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaCarousel(
    items: List<Manga>,
    onItemClick: (Manga) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    LazyRow(
        state = lazyListState,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.extraLarge),
        flingBehavior = rememberSnapFlingBehavior(
            snapLayoutInfoProvider = remember(lazyListState) {
                SnapLayoutInfoProvider(
                    lazyListState = lazyListState,
                    snapPosition = SnapPosition.Start,
                )
            },
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            BackdropCard(
                manga = item,
                onClick = { onItemClick(item) },
                alignment = remember {
                    ParallaxAlignment(
                        horizontalBias = {
                            val layoutInfo = lazyListState.layoutInfo
                            val itemInfo = layoutInfo.visibleItemsInfo.first {
                                it.key == item.id
                            }

                            val adjustedOffset = itemInfo.offset - layoutInfo.viewportStartOffset
                            (adjustedOffset / itemInfo.size.toFloat()).coerceIn(-1f, 1f)
                        },
                    )
                },
                modifier = Modifier
                    .testTag("search_carousel_item")
                    .animateItem()
                    .width(156.dp)
                    .aspectRatio(2 / 3f),
            )
        }
    }
}

@Composable
fun BackdropCard(
    manga: Manga,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier,
    ) {
        BackdropCardContent(
            manga = manga,
            alignment = alignment,
        )
    }
}

@Composable
private fun BackdropCardContent(
    manga: Manga,
    alignment: Alignment = Alignment.Center,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            imageLoader = LocalImageLoader.current,
            model = manga.largeCoverUrl ?: manga.coverUrl,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alignment = alignment,
        )

        Spacer(
            Modifier
                .matchParentSize()
                .drawForegroundGradientScrim(MaterialTheme.colorScheme.surfaceDim),
        )

        Text(
            text = manga.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart),
        )
    }
}

@Stable
class ParallaxAlignment(
    private val horizontalBias: () -> Float = { 0f },
    private val verticalBias: () -> Float = { 0f },
) : Alignment {
    override fun align(
        size: IntSize,
        space: IntSize,
        layoutDirection: LayoutDirection,
    ): IntOffset {
        // Convert to Px first and only round at the end, to avoid rounding twice while calculating
        // the new positions
        val centerX = (space.width - size.width).toFloat() / 2f
        val centerY = (space.height - size.height).toFloat() / 2f
        val resolvedHorizontalBias = if (layoutDirection == LayoutDirection.Ltr) {
            horizontalBias()
        } else {
            -1 * horizontalBias()
        }

        val x = centerX * (1 + resolvedHorizontalBias)
        val y = centerY * (1 + verticalBias())
        return IntOffset(x.roundToInt(), y.roundToInt())
    }
}

/**
 * Draws a vertical gradient scrim in the foreground.
 *
 * @param color The color of the gradient scrim.
 * @param decay The exponential decay to apply to the gradient. Defaults to `3.0f` which is
 * a cubic decay.
 * @param numStops The number of color stops to draw in the gradient. Higher numbers result in
 * the higher visual quality at the cost of draw performance. Defaults to `16`.
 */
fun Modifier.drawForegroundGradientScrim(
    color: Color,
    decay: Float = 1.0f,
    numStops: Int = 16,
    startY: Float = 0f,
    endY: Float = 1f,
): Modifier = composed {
    val colors = remember(color, numStops) {
        val baseAlpha = color.alpha
        List(numStops) { i ->
            val x = i * 1f / (numStops - 1)
            val opacity = x.pow(decay)
            color.copy(alpha = baseAlpha * opacity)
        }
    }

    drawWithContent {
        drawContent()
        drawRect(
            topLeft = Offset(x = 0f, y = startY * size.height),
            size = size.copy(height = (endY - startY) * size.height),
            brush = Brush.verticalGradient(colors = colors),
        )
    }
}