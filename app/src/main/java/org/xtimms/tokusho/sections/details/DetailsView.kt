package org.xtimms.tokusho.sections.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import org.koitharu.kotatsu.parsers.model.MangaState
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.DetailsToolbar
import org.xtimms.tokusho.core.components.PreferenceItem
import org.xtimms.tokusho.core.prefs.AppSettings

const val MANGA_ID_ARGUMENT = "{mangaId}"
const val DETAILS_DESTINATION = "details/?mangaId=$MANGA_ID_ARGUMENT"

@Composable
fun DetailsView(
    coil: ImageLoader,
    navigateBack: () -> Unit,
) {

    val context = LocalContext.current
    val viewModel: DetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val chapterListState = rememberLazyListState()

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
                title = uiState.details?.toManga()?.title ?: "Unknown",
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
                    imageUrl = uiState.details?.toManga()?.largeCoverUrl ?: "",
                    title = uiState.details?.toManga()?.title ?: "",
                    author = uiState.details?.toManga()?.author ?: "",
                    artist = "",
                    state = uiState.details?.toManga()?.state ?: MangaState.FINISHED,
                    isTabletUi = false,
                    appBarPadding = topPadding,
                )
            }

            val time = viewModel.readingTime.value
            if (AppSettings.isReadingTimeEstimationEnabled() || time == null) {
                item {
                    if (time != null) {
                        PreferenceItem(
                            title = if (time.isContinue) stringResource(id = R.string.approximate_remaining_time) else stringResource(
                                id = R.string.approximate_reading_time
                            ),
                            description = time.format(context.resources),
                            icon = Icons.Outlined.Timelapse
                        )
                    }
                }
            }

            item(
                key = DetailsViewItem.DESCRIPTION_WITH_TAG,
                contentType = DetailsViewItem.DESCRIPTION_WITH_TAG,
            ) {
                ExpandableMangaDescription(
                    defaultExpandState = true,
                    description = uiState.details?.toManga()?.description ?: "",
                    tagsProvider = { uiState.details?.toManga()?.tags?.toList() },
                    onTagSearch = { },
                    onCopyTagToClipboard = { },
                )
            }
        }
    }

}