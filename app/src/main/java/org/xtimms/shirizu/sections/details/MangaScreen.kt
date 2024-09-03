package org.xtimms.shirizu.sections.details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.Manga
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.ClassicDetailsToolbar
import org.xtimms.shirizu.core.components.MangaHorizontalItem
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.VerticalFastScroller
import org.xtimms.shirizu.core.model.MangaHistory
import org.xtimms.shirizu.core.model.parcelable.ParcelableManga
import org.xtimms.shirizu.core.parser.favicon.faviconUri
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.sections.details.data.ReadingTime
import org.xtimms.shirizu.sections.details.model.HistoryInfo
import java.time.Instant

@Composable
fun MangaScreen(
    state: DetailsScreenModel.State.Success,
    snackbarHostState: SnackbarHostState,
    isTabletUi: Boolean,
    onBackClicked: () -> Unit,
    onMangaClicked: (Manga) -> Unit,
    onWebViewClicked: (() -> Unit)?,
    onWebViewLongClicked: (() -> Unit)?,
    onTrackingClicked: () -> Unit,

    // For tags menu
    onTagSearch: (String) -> Unit,

    onFilterButtonClicked: () -> Unit,
    onRefresh: () -> Unit,
    onContinueReading: () -> Unit,

    // For cover dialog
    onCoverClicked: () -> Unit,
) {
    if (!isTabletUi) {
        MangaScreenSmallImpl(
            state = state,
            snackbarHostState = snackbarHostState,
            onBackClicked = onBackClicked,
            onTagSearch = onTagSearch,
            onMangaClicked = onMangaClicked,
            onRefresh = onRefresh,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaScreenSmallImpl(
    state: DetailsScreenModel.State.Success,
    snackbarHostState: SnackbarHostState,
    onBackClicked: () -> Unit,
    onTagSearch: (String) -> Unit,
    onMangaClicked: (Manga) -> Unit,
    onRefresh: () -> Unit,
) {
    val chapterListState = rememberLazyListState()

    BackHandler(onBack = { onBackClicked() })

    Scaffold(
        topBar = {
            val isFirstItemVisible by remember {
                derivedStateOf { chapterListState.firstVisibleItemIndex == 0 }
            }
            val isFirstItemScrolled by remember {
                derivedStateOf { chapterListState.firstVisibleItemScrollOffset > 0 }
            }
            val animatedTitleAlpha by animateFloatAsState(
                if (!isFirstItemVisible) 1f else 0f,
                label = "Top Bar Title",
            )
            val animatedBgAlpha by animateFloatAsState(
                if (!isFirstItemVisible || isFirstItemScrolled) 1f else 0f,
                label = "Top Bar Background",
            )
            ClassicDetailsToolbar(
                title = state.details.toManga().title,
                titleAlphaProvider = { animatedTitleAlpha },
                backgroundAlphaProvider = { animatedBgAlpha },
                navigateBack = { onBackClicked() },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        val topPadding = contentPadding.calculateTopPadding()

        val layoutDirection = LocalLayoutDirection.current
        val relatedMangaListState = rememberLazyListState()

        VerticalFastScroller(
            listState = chapterListState,
            topContentPadding = topPadding,
            endContentPadding = contentPadding.calculateEndPadding(layoutDirection),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                state = chapterListState,
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(layoutDirection),
                    end = contentPadding.calculateEndPadding(layoutDirection),
                    bottom = contentPadding.calculateBottomPadding(),
                ),
            ) {
                item(
                    key = DetailsScreenItem.INFO_BOX,
                    contentType = DetailsScreenItem.INFO_BOX,
                ) {
                    ClassicDetailsInfoBox(
                        imageUrl = state.details.toManga().largeCoverUrl ?: state.details.toManga().coverUrl,
                        favicon = state.details.toManga().source.faviconUri(),
                        title = state.details.toManga().title,
                        altTitle = state.details.toManga().altTitle,
                        author = state.details.toManga().author,
                        isNsfw = state.details.toManga().isNsfw,
                        state = state.details.toManga().state,
                        source = state.details.toManga().source,
                        historyInfo = null,
                        readingTime = null,
                        isTabletUi = false,
                        appBarPadding = topPadding,
                        onCoverClick = {  },
                        isInShelf = true,
                        onAddToShelfClicked = {  },
                        onSourceClicked = {  },
                        onDownloadClick = {  }
                    )
                }

                item(
                    key = DetailsScreenItem.DESCRIPTION_WITH_TAG,
                    contentType = DetailsScreenItem.DESCRIPTION_WITH_TAG,
                ) {
                    ExpandableMangaDescription(
                        defaultExpandState = false,
                        description = state.details.toManga().description,
                        tagsProvider = { state.details.toManga().tags },
                        onTagSearch = onTagSearch,
                        onCopyTagToClipboard = {  },
                    )
                }

                /*item {
                    AnimatedVisibility(
                        visible = state.relatedManga.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Text(
                                modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                                text = stringResource(id = R.string.related_manga),
                                style = MaterialTheme.typography.titleLarge
                            )
                            LazyRow(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .sizeIn(minHeight = 100.dp),
                                state = relatedMangaListState,
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = relatedMangaListState)
                            ) {
                                items(
                                    items = state.relatedManga,
                                    key = { it.id },
                                    contentType = { it }
                                ) {
                                    MangaHorizontalItem(
                                        manga = it,
                                        onClick = { manga -> onMangaClicked(manga) },
                                        onLongClick = { })
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(16.dp))
                        }
                    }
                }*/

                item {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 8.dp),
                        text = stringResource(id = R.string.chapters),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                /*items(
                    items = state.chapters
                ) {
                    ChapterListItem(
                        title = it.chapter.name,
                        date = it.chapter.uploadDate,
                        scanlator = it.chapter.scanlator,
                        read = !it.isUnread,
                        bookmark = false,
                        selected = false,
                        onLongClick = { *//*TODO*//* },
                        onClick = { *//*TODO*//* }
                    )
                }*/
            }
        }
    }
}