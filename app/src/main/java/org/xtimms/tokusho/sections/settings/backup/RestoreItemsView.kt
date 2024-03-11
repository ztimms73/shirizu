package org.xtimms.tokusho.sections.settings.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferencesHintCard
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.updates.Updater
import org.xtimms.tokusho.sections.settings.about.ProgressIndicatorButton
import org.xtimms.tokusho.utils.DeviceUtil
import org.xtimms.tokusho.utils.system.suspendToast

const val RESTORE_ARGUMENT = "{source}"
const val RESTORE_DESTINATION = "restore/?file=${RESTORE_ARGUMENT}"

@Composable
fun RestoreItemsView(
    uri: String,
    restoreViewModel: RestoreViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {

    val items = restoreViewModel.availableEntries.collectAsStateWithLifecycle()

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.restore_from_backup),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            if (DeviceUtil.isMiui && DeviceUtil.isMiuiOptimizationDisabled()) {
                item {
                    PreferencesHintCard(
                        title = stringResource(id = R.string.restore_miui_warning),
                        icon = null
                    )
                }
            }
            item {
                PreferencesHintCard(
                    title = stringResource(id = R.string.backup_creation_date),
                    description = restoreViewModel.backupDate.value.toString(),
                    icon = Icons.Outlined.AccessTime
                )
            }
            items(
                count = 5
            ) {
                BackupItem(
                    title = it.toString()
                )
            }
            item {
                var isLoading by remember { mutableStateOf(false) }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProgressIndicatorButton(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 6.dp)
                            .padding(bottom = 12.dp),
                        text = stringResource(
                            id = R.string.restore
                        ),
                        icon = Icons.Outlined.Restore,
                        isLoading = isLoading
                    ) {
                        restoreViewModel.restore()
                    }
                }
            }
        }
    }

}