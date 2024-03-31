package org.xtimms.tokusho.sections.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.MangaCover
import org.xtimms.tokusho.sections.feed.model.FeedItem
import org.xtimms.tokusho.utils.composable.selectedBackground

const val ReadItemAlpha = .38f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedViewItem(
    coil: ImageLoader,
    selected: Boolean,
    feed: FeedItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val haptic = LocalHapticFeedback.current
    val textAlpha = if (feed.isNew) 1f else ReadItemAlpha

    Row(
        modifier = modifier
            .selectedBackground(selected)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    onLongClick()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
            )
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MangaCover.Square(
            coil = coil,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxHeight(),
            data = feed.manga.coverUrl,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
        ) {
            Text(
                text = feed.manga.title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(alpha = textAlpha),
                overflow = TextOverflow.Ellipsis,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                var textHeight by remember { mutableIntStateOf(0) }
                if (feed.isNew) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = stringResource(R.string.unread),
                        modifier = Modifier
                            .height(8.dp)
                            .padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = pluralStringResource(
                        id = R.plurals.new_chapters,
                        feed.count,
                        feed.count
                    ),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = textAlpha),
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textHeight = it.size.height },
                    modifier = Modifier
                        .weight(weight = 1f, fill = false),
                )
            }
        }
    }

}