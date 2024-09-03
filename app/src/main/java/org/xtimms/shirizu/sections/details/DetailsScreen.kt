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
import org.xtimms.shirizu.core.model.parcelable.ParcelableManga
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.utils.lang.AssistContentScreen
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.isTabletUi
import javax.inject.Inject
import javax.inject.Singleton

class DetailsScreen(
    private val mangaId: Long,
    val fromSource: Boolean = false,
) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val haptic = LocalHapticFeedback.current
        val scope = rememberCoroutineScope()

        val screenModel =
            getScreenModel<DetailsScreenModel, DetailsScreenModel.Factory> { factory ->
                factory.create(context, mangaId, SnackbarHostState())
            }

        val state by screenModel.state.collectAsState()

        if (state is DetailsScreenModel.State.Loading) {
            LoadingScreen()
            return
        }

        val successState = state as DetailsScreenModel.State.Success

        MangaScreen(
            state = successState,
            snackbarHostState = screenModel.snackbarHostState,
            isTabletUi = isTabletUi(),
            onBackClicked = navigator::pop,
            onMangaClicked = {  },
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
}