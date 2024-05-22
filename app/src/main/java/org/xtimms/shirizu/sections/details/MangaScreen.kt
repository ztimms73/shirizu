package org.xtimms.shirizu.sections.details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import coil.ImageLoader
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.ClassicDetailsToolbar
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.components.VerticalFastScroller
import org.xtimms.shirizu.core.model.MangaHistory
import org.xtimms.shirizu.core.parser.favicon.faviconUri
import org.xtimms.shirizu.sections.details.data.ReadingTime
import org.xtimms.shirizu.sections.details.model.HistoryInfo
import java.time.Instant

@Composable
fun MangaScreen(
    state: DetailsScreenModel.State.Success,
    snackbarHostState: SnackbarHostState,
    isTabletUi: Boolean,
    onBackClicked: () -> Unit,
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
                title = state.manga?.title ?: "",
                titleAlphaProvider = { animatedTitleAlpha },
                backgroundAlphaProvider = { animatedBgAlpha },
                navigateBack = { onBackClicked() },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        val topPadding = contentPadding.calculateTopPadding()

        val layoutDirection = LocalLayoutDirection.current
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
                        imageUrl = state.manga.largeCoverUrl ?: state.manga.coverUrl,
                        favicon = state.manga.source.faviconUri(),
                        title = state.manga.title,
                        altTitle = state.manga.altTitle ?: stringResource(id = R.string.unknown),
                        author = state.manga.author ?: stringResource(id = R.string.unknown),
                        isNsfw = state.manga.isNsfw,
                        state = state.manga.state,
                        source = state.manga.source,
                        historyInfo = state.historyInfo,
                        readingTime = state.readingTime,
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
                        description = state.manga?.description,
                        tagsProvider = { state.manga?.tags },
                        onTagSearch = onTagSearch,
                        onCopyTagToClipboard = {  },
                    )
                }
            }
        }
    }
}