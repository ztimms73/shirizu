package org.xtimms.tokusho.core.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.tokusho.core.AsyncImageImpl

private const val GridSelectedCoverAlpha = 0.76f

@Composable
fun MangaGridItem(
    coil: ImageLoader,
    manga: Manga,
    onClick: (Manga) -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
) {
    GridItemSelectable(
        manga = manga,
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box {
                AsyncImageImpl(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .aspectRatio(10F / 16F),
                    coil = coil,
                    model = manga.largeCoverUrl ?: manga.coverUrl,
                    contentDescription = null
                )
            }
            Text(
                text = manga.title,
                modifier = Modifier.padding(4.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
fun MangaHorizontalItem(
    coil: ImageLoader,
    manga: Manga,
    onClick: (Manga) -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
) {
    GridItemSelectable(
        manga = manga,
        isSelected = isSelected,
        onClick = { onClick(manga) },
        onLongClick = onLongClick,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Box {
                AsyncImageImpl(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .aspectRatio(10F / 16F)
                        .height(156.dp),
                    coil = coil,
                    model = manga.largeCoverUrl ?: manga.coverUrl,
                    contentDescription = null
                )
            }
            Text(
                text = manga.title,
                modifier = Modifier.padding(4.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

/**
 * Common cover layout to add contents to be drawn on top of the cover.
 */
@Composable
private fun MangaGridCover(
    modifier: Modifier = Modifier,
    cover: @Composable BoxScope.() -> Unit = {},
    content: @Composable (BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(MangaCover.Book.ratio),
    ) {
        cover()
        content?.invoke(this)
    }
}

/**
 * Title overlay for [MangaCompactGridItem]
 */
@Composable
private fun BoxScope.CoverTextOverlay(
    title: String,
    onClickContinueReading: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color(0xAA000000),
                ),
            )
            .fillMaxHeight(0.33f)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
    )
    Row(
        modifier = Modifier.align(Alignment.BottomStart),
        verticalAlignment = Alignment.Bottom,
    ) {
        GridItemTitle(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            title = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black,
                    blurRadius = 4f,
                ),
            ),
            minLines = 1,
        )
    }
}

@Composable
private fun GridItemTitle(
    title: String,
    style: TextStyle,
    minLines: Int,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
) {
    Text(
        modifier = modifier,
        text = title,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        minLines = minLines,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        style = style,
    )
}

/**
 * Wrapper for grid items to handle selection state, click and long click.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridItemSelectable(
    manga: Manga,
    isSelected: Boolean,
    onClick: (Manga) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                onClick = { onClick(manga) },
                onLongClick = onLongClick,
            )
            .selectedOutline(isSelected = isSelected, color = MaterialTheme.colorScheme.secondary)
            .padding(4.dp),
    ) {
        val contentColor = if (isSelected) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            LocalContentColor.current
        }
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

/**
 * @see GridItemSelectable
 */
private fun Modifier.selectedOutline(
    isSelected: Boolean,
    color: Color,
) = this then drawBehind { if (isSelected) drawRect(color = color) }