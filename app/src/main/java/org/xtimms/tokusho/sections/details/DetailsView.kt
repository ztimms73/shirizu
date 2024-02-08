package org.xtimms.tokusho.sections.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaState
import org.xtimms.tokusho.core.components.DetailsToolbar

const val MANGA_ID_ARGUMENT = "{mangaId}"
const val DETAILS_DESTINATION = "details/$MANGA_ID_ARGUMENT"

@Composable
fun DetailsView(
    coil: ImageLoader,
    mangaId: Long,
    navigateBack: () -> Unit,
) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val chapterListState = rememberLazyListState()

    LaunchedEffect(mangaId) {
        viewModel.getDetails(mangaId)
    }

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
            DetailsToolbar(
                title = "Test",
                titleAlphaProvider = { animatedTitleAlpha },
                backgroundAlphaProvider = { animatedBgAlpha },
                onBackClicked = { navigateBack() }
            )
        },
        bottomBar = {

        },
    ) { contentPadding ->
        val topPadding = contentPadding.calculateTopPadding()
        val layoutDirection = LocalLayoutDirection.current
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
                key = DetailsViewItem.INFO_BOX,
                contentType = DetailsViewItem.INFO_BOX
            ) {
                DetailsInfoBox(
                    coil = coil,
                    imageUrl = uiState.manga?.largeCoverUrl ?: "",
                    title = uiState.manga?.title ?: "",
                    author = uiState.manga?.author ?: "",
                    artist = "",
                    state = uiState.manga?.state ?: MangaState.FINISHED,
                    isTabletUi = false,
                    appBarPadding = topPadding,
                )
            }

            item(
                key = DetailsViewItem.DESCRIPTION_WITH_TAG,
                contentType = DetailsViewItem.DESCRIPTION_WITH_TAG,
            ) {
                ExpandableMangaDescription(
                    defaultExpandState = true,
                    description = uiState.manga?.description ?: "",
                    tagsProvider = { uiState.manga?.tags?.toList() },
                    onTagSearch = {  },
                    onCopyTagToClipboard = {  },
                )
            }
        }
    }

}