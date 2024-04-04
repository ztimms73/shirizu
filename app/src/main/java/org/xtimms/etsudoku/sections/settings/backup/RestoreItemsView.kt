package org.xtimms.etsudoku.sections.settings.backup

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.PreferencesHintCard
import org.xtimms.etsudoku.core.components.ScaffoldWithTopAppBar
import org.xtimms.etsudoku.sections.settings.about.ProgressIndicatorButton
import org.xtimms.etsudoku.utils.DeviceUtil

const val RESTORE_ARGUMENT = "{file}"
const val RESTORE_DESTINATION = "restore/?file=${RESTORE_ARGUMENT}"

@Composable
fun RestoreItemsView(
    uri: String,
    restoreViewModel: RestoreViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {

    val items by restoreViewModel.availableEntries.collectAsStateWithLifecycle(emptyList())
    val backupDate by restoreViewModel.backupDate.collectAsStateWithLifecycle(null)

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
                    description = backupDate.toString(),
                    icon = Icons.Outlined.AccessTime
                )
            }
            for (item in items) {
                item {
                    BackupItem(
                        title = item.name.name
                    )
                }
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
                        // restoreViewModel.restore()
                    }
                }
            }
        }
    }

}