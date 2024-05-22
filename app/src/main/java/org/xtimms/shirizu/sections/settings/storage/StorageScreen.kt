package org.xtimms.shirizu.sections.settings.storage

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NetworkWifi
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.cache.CacheDir
import org.xtimms.shirizu.core.components.PreferenceStorageHeader
import org.xtimms.shirizu.core.components.PreferenceStorageItem
import org.xtimms.shirizu.core.components.PreferencesHintCard
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.ui.screens.LoadingScreen
import org.xtimms.shirizu.data.CACHE_SIZE_MAX
import org.xtimms.shirizu.utils.lang.Screen

object StorageScreen : Screen() {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<StorageScreenModel>()
        val state by screenModel.state.collectAsState()

        ScaffoldWithTopAppBar(
            title = stringResource(R.string.storage),
            navigateBack = navigator::pop
        ) { padding ->
            state.let {
                if (it.isLoading) {
                    LoadingScreen(Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    ) {
                        item {
                            PreferenceStorageHeader(
                                used = (state.httpCacheSize + state.thumbnailsCache + state.pagesCache).toFloat(),
                                total = state.availableSpace.toFloat()
                            )
                        }
                        item {
                            PreferencesHintCard(
                                title = stringResource(id = R.string.free_up_space),
                                description = stringResource(id = R.string.free_up_space_hint),
                                icon = Icons.Outlined.CleaningServices
                            ) {
                                screenModel.showCleanDialog(
                                    state.pagesCache,
                                    state.thumbnailsCache,
                                    state.availableSpace,
                                    state.httpCacheSize
                                )
                            }
                        }
                        item {
                            PreferenceStorageItem(
                                total = state.availableSpace.toFloat(),
                                title = stringResource(id = R.string.saved_manga),
                                icon = Icons.Outlined.SdStorage,
                            )
                        }
                        item {
                            PreferenceStorageItem(
                                total = state.availableSpace.toFloat(),
                                title = stringResource(id = R.string.pages_cache),
                                icon = Icons.Outlined.AutoStories,
                                used = state.pagesCache.toFloat()
                            )
                        }
                        item {
                            PreferenceStorageItem(
                                total = state.availableSpace.toFloat(),
                                title = stringResource(id = R.string.thumbnails_cache),
                                icon = Icons.Outlined.Image,
                                used = state.thumbnailsCache.toFloat()
                            )
                        }
                        item {
                            PreferenceStorageItem(
                                total = CACHE_SIZE_MAX.toFloat(),
                                title = stringResource(id = R.string.network_cache),
                                icon = Icons.Outlined.NetworkWifi,
                                used = state.httpCacheSize.toFloat()
                            )
                        }
                    }
                }
            }
            state.dialog?.let {
                CleanDialog(
                    isPagesCacheSelected = false,
                    isNetworkCacheSelected = false,
                    isThumbnailsCacheSelected = false,
                    onDismissRequest = screenModel::closeDialog,
                    onConfirm = { isPagesCacheSelected, isThumbnailCacheSelected, isNetworkCacheSelected ->
                        if (isPagesCacheSelected) screenModel.clearCache(CacheDir.PAGES)
                        if (isThumbnailCacheSelected) screenModel.clearCache(CacheDir.THUMBS)
                        if (isNetworkCacheSelected) screenModel.clearHttpCache()
                    }
                )
            }
        }
    }
}