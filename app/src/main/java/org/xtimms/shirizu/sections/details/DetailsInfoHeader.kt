package org.xtimms.shirizu.sections.details

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.model.MangaState
import org.koitharu.kotatsu.parsers.model.MangaTag
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.AsyncImageImpl
import org.xtimms.shirizu.core.components.AnimatedButton
import org.xtimms.shirizu.core.components.ButtonType
import org.xtimms.shirizu.core.components.HtmlTextField
import org.xtimms.shirizu.core.components.MangaCover
import org.xtimms.shirizu.core.components.ReadButton
import org.xtimms.shirizu.sections.details.data.ReadingTime
import org.xtimms.shirizu.sections.details.model.HistoryInfo
import org.xtimms.shirizu.utils.composable.clickableNoIndication
import org.xtimms.shirizu.utils.composable.secondaryItemAlpha
import kotlin.math.roundToInt

private val whitespaceLineRegex = Regex("[\\r\\n]{2,}", setOf(RegexOption.MULTILINE))

@Composable
fun MangaAndSourceTitlesLarge(
    coil: ImageLoader,
    appBarPadding: Dp,
    imageUrl: String,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    source: MangaSource,
    state: MangaState?,
    historyInfo: HistoryInfo,
    readingTime: ReadingTime?,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
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
            coil = coil,
            modifier = Modifier.fillMaxWidth(0.65f),
            data = imageUrl,
            contentDescription = stringResource(R.string.manga_cover),
        )
        Spacer(modifier = Modifier.height(16.dp))
        DetailsContentInfo(
            coil = coil,
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

@Composable
fun MangaAndSourceTitlesSmall(
    coil: ImageLoader,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    state: MangaState?,
    source: MangaSource,
    historyInfo: HistoryInfo,
    readingTime: ReadingTime?,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onSourceClicked: () -> Unit,
    onDownloadClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            DetailsContentInfo(
                coil = coil,
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
}

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun DetailsContentInfo(
    coil: ImageLoader,
    favicon: Uri,
    title: String,
    altTitle: String,
    author: String,
    state: MangaState?,
    source: String?,
    historyInfo: HistoryInfo,
    readingTime: ReadingTime?,
    isInShelf: Boolean,
    onAddToShelfClicked: () -> Unit,
    onSourceClicked: () -> Unit,
    onDownloadClick: () -> Unit,
    textAlign: TextAlign? = LocalTextStyle.current.textAlign,
) {
    Row {
        Column(
            modifier = Modifier
                .padding(end = 16.dp, start = 16.dp)
        ) {
            val sourceTitle = source?.takeIf { it.isNotBlank() }
                ?: stringResource(id = R.string.unknown)
            Text(
                text = title.ifBlank { stringResource(id = R.string.unknown_title) },
                style = MaterialTheme.typography.headlineLarge,
                textAlign = textAlign,
                lineHeight = 36.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )

            if (altTitle.isNotBlank()) {
                Text(
                    text = altTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = textAlign,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (author.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(MaterialTheme.typography.titleLarge.fontSize.value.dp),
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null
                    )
                    Text(
                        text = author,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = textAlign,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        .size(MaterialTheme.typography.bodyLarge.fontSize.value.dp),
                )
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

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    InputChip(
                        selected = false,
                        onClick = { onAddToShelfClicked() },
                        label = {
                            Text(
                                text = if (isInShelf)
                                    stringResource(id = R.string.in_shelf)
                                else
                                    stringResource(id = R.string.add_to_shelf),
                                color = if (isInShelf)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                imageVector = Icons.Outlined.LocalLibrary,
                                contentDescription = null,
                                tint = if (isInShelf)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (isInShelf)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline
                            )
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isInShelf) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    )
                    AssistChip(
                        onClick = { onSourceClicked() },
                        leadingIcon = {
                            AsyncImageImpl(
                                coil = coil,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(100)),
                                model = favicon,
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = sourceTitle) },
                    )
                    OutlinedIconButton(
                        modifier = Modifier
                            .height(32.dp)
                            .width(56.dp),
                        onClick = { onAddToShelfClicked() /*TODO*/ },
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        val rotating by rememberInfiniteTransition("rotating").animateFloat(
                            label = "rotating",
                            initialValue = 360f,
                            targetValue = -360f,
                            animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Restart)
                        )
                        Icon(
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(rotating),
                            imageVector = Icons.Outlined.Sync,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val readTime = if (readingTime?.isContinue == true) {
                    readingTime.format(LocalContext.current.resources)
                } else {
                    stringResource(id = R.string.you_completed_reading)
                }
                ReadButton(historyInfo, readTime)
                AnimatedButton(
                    modifier = Modifier
                        .size(54.dp),
                    type = ButtonType.TERTIARY,
                    icon = Icons.Outlined.FileDownload,
                    onClick = onDownloadClick
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpandableMangaDescription(
    defaultExpandState: Boolean,
    description: String?,
    tagsProvider: () -> Set<MangaTag>?,
    onTagSearch: (String) -> Unit,
    onCopyTagToClipboard: (tag: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(start = 16.dp, end = 8.dp)) {
        val (expanded, onExpanded) = rememberSaveable {
            mutableStateOf(defaultExpandState)
        }
        val desc =
            description.takeIf { !it.isNullOrBlank() }
                ?: stringResource(R.string.description_placeholder)
        val trimmedDescription = remember(desc) {
            desc
                .replace(whitespaceLineRegex, "\n")
                .trimEnd()
        }
        val tags = tagsProvider()

        Text(
            text = stringResource(id = R.string.description),
            style = MaterialTheme.typography.titleLarge
        )
        AnimatedContent(
            targetState = desc,
            label = "description"
        ) {
            MangaSummary(
                expandedDescription = it,
                shrunkDescription = trimmedDescription,
                expanded = expanded,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickableNoIndication { onExpanded(!expanded) },
            )
        }
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
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tags.forEach {
                        TagsChip(
                            modifier = modifier.padding(vertical = 4.dp),
                            tag = it,
                            onClick = {
                                tagSelected = it.title
                                showMenu = true
                            },
                        )
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
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
                HtmlTextField(
                    text = expandedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    urlSpanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            },
            {
                SelectionContainer {
                    HtmlTextField(
                        text = if (expanded) expandedDescription else shrunkDescription,
                        maxLines = Int.MAX_VALUE,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.secondaryItemAlpha(),
                        urlSpanStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            },
            {
                val colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                Box(
                    modifier = Modifier.background(Brush.verticalGradient(colors = colors)),
                    contentAlignment = Alignment.Center,
                ) {
                    val image =
                        AnimatedImageVector.animatedVectorResource(R.drawable.anim_caret_down)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagsChip(
    tag: MangaTag,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        SuggestionChip(
            modifier = modifier,
            onClick = onClick,
            label = { Text(text = tag.title) },
        )
    }
}