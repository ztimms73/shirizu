package org.xtimms.shirizu.sections.details

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.model.MangaState
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ShirizuAsyncImage
import org.xtimms.shirizu.core.components.MangaCover
import org.xtimms.shirizu.sections.details.data.ReadingTime
import org.xtimms.shirizu.sections.details.model.HistoryInfo

@Composable
fun ClassicDetailsInfoBox(
    imageUrl: String,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    isNsfw: Boolean,
    state: MangaState?,
    source: MangaSource,
    historyInfo: HistoryInfo,
    readingTime: ReadingTime,
    isTabletUi: Boolean,
    appBarPadding: Dp,
    modifier: Modifier = Modifier,
    onCoverClick: () -> Unit,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onSourceClicked: () -> Unit,
    onDownloadClick: () -> Unit,
) {

    Box(modifier = modifier) {
        // Backdrop
        val backdropGradientColors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background,
        )
        ShirizuAsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(colors = backdropGradientColors),
                    )
                }
                .blur(5.dp)
                .alpha(0.33f),
        )

        // Manga & source info
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            if (!isTabletUi) {
                MangaInfoSmall(
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
                    onCoverClick = onCoverClick,
                    onSourceClicked = onSourceClicked,
                    historyInfo = historyInfo,
                    readingTime = readingTime,
                    onDownloadClick = onDownloadClick
                )
            } else {
                MangaInfoLarge(
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
                    onCoverClick = onCoverClick,
                    onSourceClicked = onSourceClicked,
                    historyInfo = historyInfo,
                    readingTime = readingTime,
                    onDownloadClick = onDownloadClick
                )
            }
        }
    }
}

@Composable
fun MangaInfoLarge(
    appBarPadding: Dp,
    imageUrl: String,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    source: MangaSource,
    state: MangaState?,
    historyInfo: HistoryInfo?,
    readingTime: ReadingTime?,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onCoverClick: () -> Unit,
    onSourceClicked: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = appBarPadding + 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MangaCover.Book(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .clickable(
                    role = Role.Button,
                    onClick = onCoverClick
                ),
            data = imageUrl,
            contentDescription = stringResource(R.string.manga_cover),
        )
        Spacer(modifier = Modifier.height(16.dp))
        DetailsContentInfo(
            favicon = favicon,
            title = title,
            altTitle = altTitle,
            author = author,
            state = state,
            source = source.title,
            isInShelf = isInShelf,
            onAddToShelfClicked = onAddToShelfClicked,
            onSourceClicked = onSourceClicked,
            historyInfo = historyInfo!!,
            readingTime = readingTime,
            onDownloadClick = onDownloadClick
        )
    }
}

@Composable
fun MangaInfoSmall(
    appBarPadding: Dp,
    imageUrl: String,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    state: MangaState?,
    source: MangaSource,
    historyInfo: HistoryInfo,
    readingTime: ReadingTime,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onCoverClick: () -> Unit,
    onSourceClicked: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = appBarPadding + 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShirizuAsyncImage(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .sizeIn(maxWidth = 54.dp)
                .align(Alignment.Start)
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickable(
                    role = Role.Button,
                    onClick = onCoverClick
                ),
            model = imageUrl,
            contentDescription = stringResource(R.string.manga_cover),
            contentScale = ContentScale.Crop
        )
        DetailsContentInfo(
            favicon = favicon,
            title = title,
            altTitle = altTitle,
            author = author,
            state = state,
            source = source.title,
            isInShelf = isInShelf,
            onAddToShelfClicked = onAddToShelfClicked,
            onSourceClicked = onSourceClicked,
            historyInfo = historyInfo,
            readingTime = readingTime,
            onDownloadClick = onDownloadClick
        )
    }
}