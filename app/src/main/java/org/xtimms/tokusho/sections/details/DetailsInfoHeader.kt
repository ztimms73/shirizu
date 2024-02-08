package org.xtimms.tokusho.sections.details

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import org.koitharu.kotatsu.parsers.model.MangaState
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.MangaCover
import org.xtimms.tokusho.ui.theme.TokushoTheme
import org.xtimms.tokusho.utils.composable.clickableNoIndication
import org.xtimms.tokusho.utils.composable.secondaryItemAlpha
import kotlin.math.roundToInt

private val whitespaceLineRegex = Regex("[\\r\\n]{2,}", setOf(RegexOption.MULTILINE))

@Composable
fun DetailsInfoBox(
    coil: ImageLoader,
    imageUrl: String,
    title: String,
    author: String?,
    artist: String?,
    state: MangaState?,
    isTabletUi: Boolean,
    appBarPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val backdropGradientColors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background,
        )
        AsyncImage(
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
                .blur(2.dp)
                .alpha(0.2f)
        )

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            if (!isTabletUi) {
                MangaAndSourceTitlesSmall(
                    coil = coil,
                    appBarPadding = appBarPadding,
                    imageUrl = imageUrl,
                    title = title,
                    author = author,
                    artist = artist,
                    state = state
                )
            } else {
                MangaAndSourceTitlesLarge(
                    coil = coil,
                    appBarPadding = appBarPadding,
                    imageUrl = imageUrl,
                    title = title,
                    author = author,
                    artist = artist,
                    state = state
                )
            }
        }
    }
}

@Composable
private fun MangaAndSourceTitlesLarge(
    coil: ImageLoader,
    appBarPadding: Dp,
    imageUrl: String,
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
            coil = coil,
            modifier = Modifier.fillMaxWidth(0.65f),
            data = imageUrl,
            contentDescription = stringResource(R.string.manga_cover),
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
    coil: ImageLoader,
    appBarPadding: Dp,
    imageUrl: String,
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
                coil = coil,
                modifier = Modifier
                    .sizeIn(maxWidth = 100.dp)
                    .align(Alignment.Top),
                data = imageUrl,
                contentDescription = stringResource(R.string.manga_cover),
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
        modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .secondaryItemAlpha()
            .padding(bottom = 16.dp),
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
        modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .secondaryItemAlpha(),
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
        modifier = Modifier
            .weight(1f)
            .wrapContentSize()
            .secondaryItemAlpha(),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpandableMangaDescription(
    defaultExpandState: Boolean,
    description: String?,
    tagsProvider: () -> List<MangaTag>?,
    onTagSearch: (String) -> Unit,
    onCopyTagToClipboard: (tag: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val (expanded, onExpanded) = rememberSaveable {
            mutableStateOf(defaultExpandState)
        }
        val desc =
            description.takeIf { !it.isNullOrBlank() } ?: stringResource(R.string.description_placeholder)
        val trimmedDescription = remember(desc) {
            desc
                .replace(whitespaceLineRegex, "\n")
                .trimEnd()
        }
        val tags = tagsProvider()
        if (!tags.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .animateContentSize(),
            ) {
                var showMenu by remember { mutableStateOf(false) }
                var tagSelected by remember { mutableStateOf("") }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.search)) },
                        onClick = {
                            onTagSearch(tagSelected)
                            showMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.action_copy_to_clipboard)) },
                        onClick = {
                            onCopyTagToClipboard(tagSelected)
                            showMenu = false
                        },
                    )
                }
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    tags.forEach {
                        TagsChip(
                            modifier = DefaultTagChipModifier,
                            text = it.title,
                            onClick = {
                                tagSelected = it.title
                                showMenu = true
                            },
                        )
                    }
                }
            }
        }
        MangaSummary(
            expandedDescription = desc,
            shrunkDescription = trimmedDescription,
            expanded = expanded,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickableNoIndication { onExpanded(!expanded) },
        )
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun MangaSummary(
    expandedDescription: String,
    shrunkDescription: String,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val animProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        label = "summary",
    )
    Layout(
        modifier = modifier.clipToBounds(),
        contents = listOf(
            {
                Text(
                    text = "\n\n", // Shows at least 3 lines
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            {
                Text(
                    text = expandedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            {
                SelectionContainer {
                    Text(
                        text = if (expanded) expandedDescription else shrunkDescription,
                        maxLines = Int.MAX_VALUE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.secondaryItemAlpha(),
                    )
                }
            },
            {
                val colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                Box(
                    modifier = Modifier.background(Brush.verticalGradient(colors = colors)),
                    contentAlignment = Alignment.Center,
                ) {
                    val image = AnimatedImageVector.animatedVectorResource(R.drawable.anim_caret_down)
                    Icon(
                        painter = rememberAnimatedVectorPainter(image, !expanded),
                        contentDescription = stringResource(
                            if (expanded) R.string.manga_info_collapse else R.string.manga_info_expand,
                        ),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.background(Brush.radialGradient(colors = colors.asReversed())),
                    )
                }
            },
        ),
    ) { (shrunk, expanded, actual, scrim), constraints ->
        val shrunkHeight = shrunk.single()
            .measure(constraints)
            .height
        val expandedHeight = expanded.single()
            .measure(constraints)
            .height
        val heightDelta = expandedHeight - shrunkHeight
        val scrimHeight = 24.dp.roundToPx()

        val actualPlaceable = actual.single()
            .measure(constraints)
        val scrimPlaceable = scrim.single()
            .measure(Constraints.fixed(width = constraints.maxWidth, height = scrimHeight))

        val currentHeight = shrunkHeight + ((heightDelta + scrimHeight) * animProgress).roundToInt()
        layout(constraints.maxWidth, currentHeight) {
            actualPlaceable.place(0, 0)

            val scrimY = currentHeight - scrimHeight
            scrimPlaceable.place(0, scrimY)
        }
    }
}

private val DefaultTagChipModifier = Modifier.padding(vertical = 4.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        SuggestionChip(
            modifier = modifier,
            onClick = onClick,
            label = { Text(text = text) },
        )
    }
}