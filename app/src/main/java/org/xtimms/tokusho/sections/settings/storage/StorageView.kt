package org.xtimms.tokusho.sections.settings.storage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.NetworkWifi
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
        if (!uiState.isLoading) LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item {
                PreferenceStorageHeader(
                    used = uiState.httpCacheSize + uiState.thumbnailsCache + uiState.pagesCache,
                    total = uiState.availableSpace
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
                    total = uiState.availableSpace,
                    title = stringResource(id = R.string.saved_manga),
                    icon = Icons.Outlined.SdStorage
                )
            }
            item {
                PreferenceStorageItem(
                    total = uiState.availableSpace,
                    title = stringResource(id = R.string.pages_cache),
                    icon = Icons.Outlined.AutoStories,
                    used = uiState.pagesCache
                )
            }
            item {
                PreferenceStorageItem(
                    total = uiState.availableSpace,
                    title = stringResource(id = R.string.thumbnails_cache),
                    icon = Icons.Outlined.Image,
                    used = uiState.thumbnailsCache
                )
            }
            item {
                PreferenceStorageItem(
                    total = uiState.availableSpace,
                    title = stringResource(id = R.string.network_cache),
                    icon = Icons.Outlined.NetworkWifi,
                    used = uiState.httpCacheSize
                )
            }
        } else Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
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