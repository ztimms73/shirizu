package org.xtimms.tokusho.sections.settings.storage

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.cache.CacheDir
import org.xtimms.tokusho.core.components.PreferenceStorageHeader
import org.xtimms.tokusho.core.components.PreferenceStorageItem
import org.xtimms.tokusho.core.components.PreferencesHintCard
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.data.CACHE_SIZE_MAX

const val STORAGE_DESTINATION = "storage"

@Composable
fun StorageView(
    navigateBack: () -> Unit,
) {

    val viewModel: StorageViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showCleanDialog by remember { mutableStateOf(false) }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.storage),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                PreferenceStorageHeader(
                    used = (uiState.httpCacheSize + uiState.thumbnailsCache + uiState.pagesCache).toFloat(),
                    total = uiState.availableSpace.toFloat()
                )
            }
            item {
                PreferencesHintCard(
                    title = stringResource(id = R.string.free_up_space),
                    description = stringResource(id = R.string.free_up_space_hint),
                    icon = Icons.Outlined.CleaningServices
                ) {
                    showCleanDialog = true
                }
            }
            item {
                PreferenceStorageItem(
                    total = uiState.availableSpace.toFloat(),
                    title = stringResource(id = R.string.saved_manga),
                    icon = Icons.Outlined.SdStorage
                )
            }
            item {
                PreferenceStorageItem(
                    total = uiState.availableSpace.toFloat(),
                    title = stringResource(id = R.string.pages_cache),
                    icon = Icons.Outlined.AutoStories,
                    used = uiState.pagesCache.toFloat()
                )
            }
            item {
                PreferenceStorageItem(
                    total = uiState.availableSpace.toFloat(),
                    title = stringResource(id = R.string.thumbnails_cache),
                    icon = Icons.Outlined.Image,
                    used = uiState.thumbnailsCache.toFloat()
                )
            }
            item {
                PreferenceStorageItem(
                    total = CACHE_SIZE_MAX.toFloat(),
                    title = stringResource(id = R.string.network_cache),
                    icon = Icons.Outlined.NetworkWifi,
                    used = uiState.httpCacheSize.toFloat()
                )
            }
        }
    }
    if (showCleanDialog) {
        CleanDialog(
            onDismissRequest = { showCleanDialog = false },
            isPagesCacheSelected = false,
            isNetworkCacheSelected = false,
            isThumbnailsCacheSelected = false,
            onConfirm = { isPagesCacheSelected, isThumbnailCacheSelected, isNetworkCacheSelected ->
                if (isPagesCacheSelected) viewModel.clearCache(CacheDir.PAGES)
                if (isThumbnailCacheSelected) viewModel.clearCache(CacheDir.THUMBS)
                if (isNetworkCacheSelected) viewModel.clearHttpCache()
            }
        )
    }
}