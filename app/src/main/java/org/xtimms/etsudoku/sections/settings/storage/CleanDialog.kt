package org.xtimms.etsudoku.sections.settings.storage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.ConfirmButton
import org.xtimms.etsudoku.core.components.DialogCheckBoxItem
import org.xtimms.etsudoku.core.components.DismissButton
import org.xtimms.etsudoku.core.components.EtsudokuDialog
import org.xtimms.etsudoku.utils.FileSize

@Composable
fun CleanDialog(
    onDismissRequest: () -> Unit = {},
    isPagesCacheSelected: Boolean,
    isThumbnailsCacheSelected: Boolean,
    isNetworkCacheSelected: Boolean,
    onConfirm: (isPagesCacheSelected: Boolean, isThumbnailCacheSelected: Boolean, isNetworkCacheSelected: Boolean) -> Unit = { _, _, _ -> }
) {

    val viewModel: StorageViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var pagesCache by remember {
        mutableStateOf(isPagesCacheSelected)
    }
    var thumbnailsCache by remember {
        mutableStateOf(isThumbnailsCacheSelected)
    }
    var networkCache by remember {
        mutableStateOf(isNetworkCacheSelected)
    }

    EtsudokuDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            ConfirmButton {
                onConfirm(pagesCache, thumbnailsCache, networkCache)
                onDismissRequest()
            }
        },
        dismissButton = {
            DismissButton {
                onDismissRequest()
            }
        },
        title = {
            Text(
                text = stringResource(
                    id = R.string.free_up_space
                )
            )
        },
        icon = { Icon(imageVector = Icons.Outlined.CleaningServices, contentDescription = null) },
        text = {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
                DialogCheckBoxItem(
                    text = stringResource(id = R.string.pages_cache),
                    checked = pagesCache
                ) {
                    pagesCache = !pagesCache
                }
                DialogCheckBoxItem(
                    text = stringResource(id = R.string.thumbnails_cache),
                    checked = thumbnailsCache
                ) {
                    thumbnailsCache = !thumbnailsCache
                }
                DialogCheckBoxItem(
                    text = stringResource(id = R.string.network_cache),
                    checked = networkCache
                ) {
                    networkCache = !networkCache
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
                Spacer(modifier = Modifier.height(4.dp))
                val summary = StringBuilder().run {
                    append(
                        FileSize.BYTES.format(
                            LocalContext.current,
                            (uiState.pagesCache + uiState.thumbnailsCache + uiState.httpCacheSize).toFloat()
                        )
                    )
                    append("")
                }
                Text(
                    text = stringResource(R.string.free_up_space_summary) + " " + summary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                )
            }
        })
}

@Preview
@Composable
private fun CleanDialogPreview() {
    CleanDialog(
        onDismissRequest = {},
        isPagesCacheSelected = false,
        isThumbnailsCacheSelected = false,
        isNetworkCacheSelected = false
    )
}