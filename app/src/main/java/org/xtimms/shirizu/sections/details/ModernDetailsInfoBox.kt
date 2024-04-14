package org.xtimms.shirizu.sections.details

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.model.MangaState
import org.xtimms.shirizu.core.AsyncImageImpl
import org.xtimms.shirizu.sections.details.data.ReadingTime
import org.xtimms.shirizu.sections.details.model.HistoryInfo

@Composable
fun ModernDetailsInfoBox(
    coil: ImageLoader,
    imageUrl: String,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    isNsfw: Boolean,
    state: MangaState?,
    source: MangaSource,
    historyInfo: HistoryInfo,
    readingTime: ReadingTime?,
    isTabletUi: Boolean,
    appBarPadding: Dp,
    modifier: Modifier = Modifier,
    onCoverClick: () -> Unit,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onSourceClicked: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            AsyncImageImpl(
                coil = coil,
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .aspectRatio(1f)
                    .clickable(
                        role = Role.Button,
                        onClick = onCoverClick
                    )
                    .clip(MaterialTheme.shapes.large)
            )
            if (isNsfw) {
                ElevatedAssistChip(
                    modifier = Modifier.padding(end = 32.dp, bottom = 8.dp),
                    onClick = { /*TODO*/ },
                    label = {
                        Text(
                            text = "18+",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.errorContainer),
                    colors = AssistChipDefaults.elevatedAssistChipColors()
                        .copy(containerColor = MaterialTheme.colorScheme.errorContainer)
                )
            }
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            if (!isTabletUi) {
                MangaAndSourceTitlesSmall(
                    coil = coil,
                    favicon = favicon,
                    title = title,
                    altTitle = altTitle,
                    author = author,
                    state = state,
                    source = source,
                    isInShelf = isInShelf,
                    onAddToShelfClicked = onAddToShelfClicked,
                    onSourceClicked = onSourceClicked,
                    historyInfo = historyInfo,
                    readingTime = readingTime,
                    onDownloadClick = onDownloadClick
                )
            } else {
                MangaAndSourceTitlesLarge(
                    coil = coil,
                    appBarPadding = appBarPadding,
                    imageUrl = imageUrl,
                    favicon = favicon,
                    title = title,
                    altTitle = altTitle,
                    author = author,
                    state = state,
                    source = source,
                    isInShelf = isInShelf,
                    onAddToShelfClicked = onAddToShelfClicked,
                    onSourceClicked = onSourceClicked,
                    historyInfo = historyInfo,
                    readingTime = readingTime,
                    onDownloadClick = onDownloadClick
                )
            }
        }
    }
}