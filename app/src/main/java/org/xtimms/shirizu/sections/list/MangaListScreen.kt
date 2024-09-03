package org.xtimms.shirizu.sections.list

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.components.Scaffold
import org.xtimms.shirizu.core.model.parcelable.ParcelableManga
import org.xtimms.shirizu.sections.details.DetailsScreen
import org.xtimms.shirizu.utils.lang.Screen

@OptIn(ExperimentalMaterial3Api::class)
data class MangaListScreen(private val source: MangaSource) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val scope = rememberCoroutineScope()
        val haptic = LocalHapticFeedback.current
        val uriHandler = LocalUriHandler.current
        val snackbarHostState = remember { SnackbarHostState() }

        val screenModel = getScreenModel<MangaListScreenModel, MangaListScreenModel.Factory> { factory ->
            factory.create(source.name)
        }
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    BrowseSourceToolbar(
                        searchQuery = state.toolbarQuery,
                        onSearchQueryChange = {  },
                        source = screenModel.source,
                        navigateUp = navigator::pop,
                        onWebViewClick = {  },
                        onSearch = {  },
                    )

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {

                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { paddingValues ->
            MangaListContent(
                mangaList = state.list,
                columns = screenModel.getColumnsPreference(LocalConfiguration.current.orientation),
                snackbarHostState = snackbarHostState,
                contentPadding = paddingValues,
                onMangaClick = { navigator.push((DetailsScreen(it.id, true))) },
                onMangaLongClick = { manga -> },
            )
        }
    }
}