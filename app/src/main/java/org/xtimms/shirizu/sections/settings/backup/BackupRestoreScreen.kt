package org.xtimms.shirizu.sections.settings.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AvTimer
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.SdCardAlert
import androidx.compose.material.icons.outlined.SnippetFolder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferenceInfo
import org.xtimms.shirizu.core.components.PreferenceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitchWithContainer
import org.xtimms.shirizu.core.components.PreferencesHintCard
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.components.icons.Kotatsu
import org.xtimms.shirizu.data.repository.backup.BackupRepository
import org.xtimms.shirizu.data.repository.backup.BackupZipOutput
import org.xtimms.shirizu.utils.lang.MutableEventFlow
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.call
import org.xtimms.shirizu.utils.system.toast
import org.xtimms.shirizu.utils.system.tryLaunch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

object BackupRestoreScreen : Screen() {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val snackbarHostState = remember { SnackbarHostState() }

        var backup: File? = null

        val chooseBackupDir = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/zip"),
        ) {
            if (it != null && backup != null) {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
            }
        }

        val chooseBackupToRestore = rememberLauncherForActivityResult(
            object : ActivityResultContracts.OpenDocument() {
                override fun createIntent(context: Context, input: Array<String>): Intent {
                    val intent = super.createIntent(context, input)
                    return Intent.createChooser(intent, context.getString(R.string.file_select_backup))
                }
            },
        ) { uri ->
            if (uri == null) {
                context.toast(R.string.file_null_uri_error)
                return@rememberLauncherForActivityResult
            }

            navigator.push(RestoreBackupScreen(uri.toString()))
        }

        val showDirectoryAlert =
            Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()

        ScaffoldWithTopAppBar(
            title = stringResource(R.string.backup_and_restore),
            navigateBack = navigator::pop,
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier.systemBarsPadding(),
                    hostState = snackbarHostState
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            ) {
                if (showDirectoryAlert)
                    item {
                        PreferencesHintCard(
                            title = stringResource(R.string.permission_issue),
                            description = stringResource(R.string.permission_issue_desc),
                            icon = Icons.Outlined.SdCardAlert,
                        ) {
                            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    data = Uri.parse("package:" + context.packageName)
                                    if (resolveActivity(context.packageManager) != null)
                                        context.startActivity(this)
                                }
                            }
                        }
                    }
                item {
                    PreferencesHintCard(
                        title = stringResource(R.string.supports_kotatsu_backups),
                        description = stringResource(R.string.supports_kotatsu_backups_desc),
                        icon = Icons.Filled.Kotatsu,
                    )
                }
                item {
                    PreferenceSwitchWithContainer(
                        title = stringResource(id = R.string.enable_periodic_backups),
                        icon = null,
                        isChecked = true
                    ) {

                    }
                }
                item { PreferenceSubtitle(text = stringResource(id = R.string.general)) }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.backup_creation_frequency),
                        description = "Once per week",
                        icon = Icons.Outlined.AvTimer
                    )
                }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.backup_output_directory),
                        description = "TODO",
                        icon = Icons.Outlined.SnippetFolder
                    )
                }
                item { PreferenceSubtitle(text = stringResource(id = R.string.actions)) }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.create_data_backup),
                        description = stringResource(id = R.string.create_data_backup_desc),
                        icon = Icons.Outlined.Create,
                        trailingIcon = { UpdateProgressIndicator() }
                    ) {
                        chooseBackupDir
                    }
                }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.restore_from_backup),
                        description = stringResource(id = R.string.restore_from_backup_desc),
                        icon = Icons.Outlined.Restore
                    ) {
                        chooseBackupToRestore.launch(arrayOf("*/*"))
                    }
                }
                item { HorizontalDivider() }
                item {
                    PreferenceInfo(text = stringResource(id = R.string.backup_restore_hint))
                }
            }
        }
    }
}

@Composable
private fun UpdateProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(start = 8.dp, end = 16.dp)
            .size(24.dp)
            .padding(2.dp)
    )
}

private class BackupRestoreScreenModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: BackupRepository,
) : ScreenModel {

    val progress = MutableStateFlow(-1f)
    val onBackupDone = MutableEventFlow<File>()

    init {
        screenModelScope.launch {
            val file = BackupZipOutput(context).use { backup ->
                val step = 1f / 6f
                backup.put(repository.createIndex())

                progress.value = 0f
                backup.put(repository.dumpHistory())

                progress.value += step
                backup.put(repository.dumpCategories())

                progress.value += step
                backup.put(repository.dumpFavourites())

                progress.value += step
                backup.put(repository.dumpBookmarks())

                progress.value += step
                backup.put(repository.dumpSources())

                backup.finish()
                progress.value = 1f
                backup.file
            }
            onBackupDone.call(file)
        }
    }
}