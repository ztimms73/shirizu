package org.xtimms.tokusho.sections.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.koitharu.kotatsu.parsers.model.MangaState
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.MangaCover
import org.xtimms.tokusho.ui.theme.TokushoTheme
import org.xtimms.tokusho.utils.secondaryItemAlpha

@Composable
fun DetailsInfoBox(
    isTabletUi: Boolean,
    appBarPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val backdropGradientColors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background,
        )
        Image(
            painterResource(id = R.drawable.ookami),
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
                .blur(8.dp)
                .alpha(0.2f)
        )

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            if (!isTabletUi) {
                MangaAndSourceTitlesSmall(
                    appBarPadding = appBarPadding,
                    onCoverClick = { },
                    title = "Ookami to Koushinryou",
                    author = "Hasekura Isuna",
                    artist = "Koume Keito",
                    state = MangaState.FINISHED
                )
            } else {
                MangaAndSourceTitlesLarge(
                    appBarPadding = appBarPadding,
                    onCoverClick = { },
                    title = "Ookami to Koushinryou",
                    author = "Hasekura Isuna",
                    artist = "Koume Keito",
                    state = MangaState.FINISHED
                )
            }
        }
    }
}

@Composable
private fun MangaAndSourceTitlesLarge(
    appBarPadding: Dp,
    onCoverClick: () -> Unit,
    title: String,
    author: String?,
    artist: String?,
    state: MangaState?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = appBarPadding + 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MangaCover.Book(
            modifier = Modifier.fillMaxWidth(0.65f),
            data = painterResource(id = R.drawable.ookami),
            contentDescription = stringResource(R.string.manga_cover),
            onClick = onCoverClick,
        )
        Spacer(modifier = Modifier.height(16.dp))
        DetailsContentInfo(
            title = title,
            author = author,
            artist = artist,
        )
    }
}

@Composable
private fun MangaAndSourceTitlesSmall(
    appBarPadding: Dp,
    onCoverClick: () -> Unit,
    title: String,
    author: String?,
    artist: String?,
    state: MangaState?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = appBarPadding + 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MangaCover.Book(
                modifier = Modifier
                    .sizeIn(maxWidth = 100.dp)
                    .align(Alignment.Top),
                data = painterResource(id = R.drawable.ookami),
                contentDescription = stringResource(R.string.manga_cover),
                onClick = onCoverClick,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                DetailsContentInfo(
                    title = title,
                    author = author,
                    artist = artist,
                )
            }
        }
        Row {
            DetailsRow(
                source = "MangaDex",
                chapters = "22 chapters",
                state = state
            )
        }
    }

}

@Composable
private fun ColumnScope.DetailsContentInfo(
    title: String,
    author: String?,
    artist: String?,
    textAlign: TextAlign? = LocalTextStyle.current.textAlign,
) {
    val context = LocalContext.current
    Text(
        text = title.ifBlank { stringResource(id = R.string.unknown_title) },
        style = MaterialTheme.typography.headlineSmall,
        textAlign = textAlign
    )

    Spacer(modifier = Modifier.height(2.dp))

    Row(
        modifier = Modifier.secondaryItemAlpha(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = author?.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.unknown_author),
            style = MaterialTheme.typography.titleSmall,
            textAlign = textAlign
        )
    }

    if (!artist.isNullOrBlank() && author != artist) {
        Row(
            modifier = Modifier.secondaryItemAlpha(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Brush,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.titleSmall,
                textAlign = textAlign,
            )
        }
    }
}

@Composable
private fun RowScope.DetailsRow(
    source: String?,
    chapters: String?,
    state: MangaState?,
    textAlign: TextAlign? = LocalTextStyle.current.textAlign,
) {
    Column(
        modifier = Modifier.weight(1f).wrapContentSize().secondaryItemAlpha(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = when (state) {
                MangaState.ONGOING -> Icons.Outlined.Schedule
                MangaState.FINISHED -> Icons.Outlined.DoneAll
                MangaState.ABANDONED -> Icons.Outlined.Close
                MangaState.PAUSED -> Icons.Outlined.Pause
                MangaState.UPCOMING -> Icons.Outlined.Upcoming
                else -> Icons.Outlined.Block
            },
            contentDescription = null,
            modifier = Modifier
                .size(24.dp),
        )
        ProvideTextStyle(MaterialTheme.typography.bodySmall) {
            Text(
                text = when (state) {
                    MangaState.ONGOING -> stringResource(id = R.string.ongoing)
                    MangaState.FINISHED -> stringResource(id = R.string.finished)
                    MangaState.ABANDONED -> stringResource(id = R.string.abandoned)
                    MangaState.PAUSED -> stringResource(id = R.string.paused)
                    MangaState.UPCOMING -> stringResource(id = R.string.upcoming)
                    else -> stringResource(id = R.string.unknown)
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
    Column(
        modifier = Modifier.weight(1f).wrapContentSize().secondaryItemAlpha(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = chapters?.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.unknown),
            style = MaterialTheme.typography.bodySmall,
            textAlign = textAlign
        )
    }
    Column(
        modifier = Modifier.weight(1f).wrapContentSize().secondaryItemAlpha(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Language,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = source?.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.unknown),
            style = MaterialTheme.typography.bodySmall,
            textAlign = textAlign
        )
    }
}

@PreviewLightDark
@Composable
fun DetailsInfoBoxPreview() {
    TokushoTheme {
        DetailsInfoBox(
            isTabletUi = false,
            appBarPadding = 72.dp,
        )
    }
}