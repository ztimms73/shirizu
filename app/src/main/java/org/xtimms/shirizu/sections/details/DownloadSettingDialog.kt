package org.xtimms.shirizu.sections.details

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.DismissButton
import org.xtimms.shirizu.core.components.DrawerSheetSubtitle
import org.xtimms.shirizu.core.components.FilledButtonWithIcon
import org.xtimms.shirizu.core.components.OutlinedButtonWithIcon
import org.xtimms.shirizu.core.components.ShirizuModalBottomSheet
import org.xtimms.shirizu.core.components.SingleChoiceChip

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun DownloadSettingDialog(
    useDialog: Boolean = false,
    showDialog: Boolean = false,
    sheetState: SheetState,
    onDownloadConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
) {

    val downloadButtonCallback = {
        onDismissRequest()
        onDownloadConfirm()
    }

    val sheetContent: @Composable () -> Unit = {
        Column {
            Text(
                text = stringResource(R.string.settings_before_download_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            DrawerSheetSubtitle(text = stringResource(id = R.string.download_format))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                SingleChoiceChip(
                    selected = true,
                    onClick = {

                    },
                    label = stringResource(id = R.string.auto)
                )
                SingleChoiceChip(
                    selected = false,
                    onClick = {

                    },
                    label = stringResource(id = R.string.single_cbz)
                )
                SingleChoiceChip(
                    selected = false,
                    onClick = {

                    },
                    label = stringResource(id = R.string.multiple_cbz)
                )
            }
        }
    }

    if (showDialog) {
        if (!useDialog) {
            ShirizuModalBottomSheet(
                sheetState = sheetState,
                horizontalPadding = PaddingValues(horizontal = 20.dp),
                onDismissRequest = onDismissRequest,
                content = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            imageVector = Icons.Outlined.DoneAll,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.settings_before_download),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 16.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        sheetContent()
                        val state = rememberLazyListState()
                        LaunchedEffect(sheetState.isVisible) {
                            state.scrollToItem(0)
                        }
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            horizontalArrangement = Arrangement.End,
                            state = state,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                OutlinedButtonWithIcon(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    onClick = onDismissRequest,
                                    icon = Icons.Outlined.Cancel,
                                    text = stringResource(R.string.cancel)
                                )
                            }
                            item {
                                FilledButtonWithIcon(
                                    onClick = downloadButtonCallback,
                                    icon = Icons.Outlined.DownloadDone,
                                    text = stringResource(R.string.start_download),
                                )
                            }
                        }
                    }
                })
        } else {
            AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
                TextButton(onClick = downloadButtonCallback) {
                    Text(text = stringResource(R.string.start_download))
                }
            }, dismissButton = { DismissButton { onDismissRequest() } }, icon = {
                Icon(
                    imageVector = Icons.Outlined.DoneAll, contentDescription = null
                )
            }, title = {
                Text(
                    stringResource(R.string.settings_before_download),
                    textAlign = TextAlign.Center
                )
            }, text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    sheetContent()
                }
            })
        }
    }
}