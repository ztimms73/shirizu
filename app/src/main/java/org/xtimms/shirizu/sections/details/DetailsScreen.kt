package org.xtimms.shirizu.sections.details

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.utils.lang.AssistContentScreen
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.isTabletUi
import javax.inject.Inject
import javax.inject.Singleton

class DetailsScreen(
    private val manga: Manga,
    val fromSource: Boolean = false,
) : Screen(), AssistContentScreen {

    private var assistUrl: String? = null

    override fun onProvideAssistUrl() = assistUrl

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val haptic = LocalHapticFeedback.current
        val scope = rememberCoroutineScope()

        val screenModel =
            getScreenModel<DetailsScreenModel, DetailsScreenModel.Factory> { factory ->
                factory.create(context, manga, SnackbarHostState())
            }

        val state by screenModel.state.collectAsState()

        if (state is DetailsScreenModel.State.Loading) {
            LoadingScreen()
            return
        }

        val successState = state as DetailsScreenModel.State.Success
        val isOnlineSource = remember { successState.source != MangaSource.DUMMY && successState.source != MangaSource.LOCAL }

        MangaScreen(
            state = successState,
            snackbarHostState = screenModel.snackbarHostState,
            isTabletUi = isTabletUi(),
            onBackClicked = navigator::pop,
            onWebViewClicked = {

            },
            onWebViewLongClicked = {

            },
            onTrackingClicked = {

            },
            onTagSearch = {  },
            onFilterButtonClicked = {  },
            onRefresh = {  },
            onContinueReading = {  },
            onCoverClicked = {  },
        )
    }

    private suspend fun getMangaUrl(manga_: Manga?, parser_: MangaParser?): String? {
        val manga = manga_ ?: return null
        val source = parser_ ?: return null

        return try {
            source.getDetails(manga).publicUrl
        } catch (e: Exception) {
            null
        }
    }
}