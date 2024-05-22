package org.xtimms.shirizu.sections.settings.backup

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import org.koitharu.kotatsu.parsers.util.SuspendLazy
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferencesHintCard
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.data.repository.backup.BackupEntry
import org.xtimms.shirizu.data.repository.backup.BackupRepository
import org.xtimms.shirizu.data.repository.backup.BackupZipInput
import org.xtimms.shirizu.data.repository.backup.CompositeResult
import org.xtimms.shirizu.sections.settings.about.ProgressIndicatorButton
import org.xtimms.shirizu.utils.DeviceUtil
import org.xtimms.shirizu.utils.lang.Screen
import java.io.File
import java.io.FileNotFoundException
import java.util.Date
import java.util.EnumMap
import java.util.EnumSet

class RestoreBackupScreen(
    private val uri: String,
) : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val model = getScreenModel<RestoreBackupScreenModel, RestoreBackupScreenModel.Factory> { factory ->
            factory.create(uri)
        }
        val state by model.state.collectAsState()

        /*Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(MR.strings.pref_restore_backup),
                    navigateUp = navigator::pop,
                    scrollBehavior = it,
                )
            },
        ) { contentPadding ->
            LazyColumnWithAction(
                contentPadding = contentPadding,
                actionLabel = stringResource(MR.strings.action_restore),
                actionEnabled = state.canRestore && state.options.anyEnabled(),
                onClickAction = {
                    model.startRestore()
                    navigator.pop()
                },
            ) {
                if (DeviceUtil.isMiui && DeviceUtil.isMiuiOptimizationDisabled()) {
                    item {
                        WarningBanner(MR.strings.restore_miui_warning)
                    }
                }

                if (state.canRestore) {
                    item {
                        SectionCard {
                            RestoreOptions.options.forEach { option ->
                                LabeledCheckbox(
                                    label = stringResource(option.label),
                                    checked = option.getter(state.options),
                                    onCheckedChange = {
                                        model.toggle(option.setter, it)
                                    },
                                )
                            }
                        }
                    }
                }

                if (state.error != null) {
                    errorMessageItem(state.error)
                }
            }
        }*/
        ScaffoldWithTopAppBar(
            title = stringResource(R.string.restore_from_backup),
            navigateBack = navigator::pop
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
                        description = state.backupDate.toString(),
                        icon = Icons.Outlined.AccessTime
                    )
                }
                for (item in model.availableEntries.value) {
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
                            model.restore(uri.toUri())
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            model.events.collectLatest { event ->
                when (event) {
                    RestoreBackupScreenModel.Event.RestoreDone -> run {
                        navigator::pop
                    }
                    RestoreBackupScreenModel.Event.InternalError -> {
                        launch {  }
                    }
                }
            }
        }
    }
}

class RestoreBackupScreenModel @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val uri: String,
    private val repository: BackupRepository,
) : StateScreenModel<RestoreBackupScreenModel.State>(State()) {

    private val _events: Channel<Event> = Channel(Channel.UNLIMITED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private val backupInput = SuspendLazy {
        val contentResolver = context.contentResolver
        runInterruptible(Dispatchers.IO) {
            val tempFile = File.createTempFile("backup_", ".tmp")
            (contentResolver.openInputStream(uri.toUri()) ?: throw FileNotFoundException()).use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            BackupZipInput(tempFile)
        }
    }

    val progress = MutableStateFlow(-1f)

    val availableEntries = MutableStateFlow<List<BackupEntryModel>>(emptyList())
    val backupDate = MutableStateFlow<Date?>(null)

    init {
        screenModelScope.launch(Dispatchers.Default) {
            val backup = backupInput.get()
            val entries = backup.entries()
            availableEntries.value = BackupEntry.Name.entries.mapNotNull { entry ->
                if (entry == BackupEntry.Name.INDEX || entry !in entries) {
                    return@mapNotNull null
                }
                BackupEntryModel(
                    name = entry,
                    isChecked = true,
                    isEnabled = true,
                )
            }
            backupDate.value = repository.getBackupDate(backup.getEntry(BackupEntry.Name.INDEX))
        }
    }

    fun onItemClick(item: BackupEntryModel) {
        val map =
            availableEntries.value.associateByTo(EnumMap(BackupEntry.Name::class.java)) { it.name }
        map[item.name] = item.copy(isChecked = !item.isChecked)
        map.validate()
        availableEntries.value = map.values.sortedBy { it.name.ordinal }
    }

    fun restore(uri: Uri) {
        screenModelScope.launch {
            val contentResolver = context.contentResolver
            val tempFile = File.createTempFile("backup_", ".tmp")
            (contentResolver.openInputStream(uri) ?: throw FileNotFoundException()).use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val backupInput = BackupZipInput(tempFile)
            val backup: BackupZipInput = backupInput
            val checkedItems =
                availableEntries.value.mapNotNullTo(EnumSet.noneOf(BackupEntry.Name::class.java)) {
                    if (it.isChecked) it.name else null
                }
            val result = CompositeResult()
            val step = 1f / 5f

            progress.value = 0f
            //if (BackupEntry.Name.HISTORY in checkedItems) {
            backup.getEntry(BackupEntry.Name.HISTORY)?.let {
                result += repository.restoreHistory(it)
            }
            //}

            progress.value += step
            //if (BackupEntry.Name.CATEGORIES in checkedItems) {
            backup.getEntry(BackupEntry.Name.CATEGORIES)?.let {
                result += repository.restoreCategories(it)
            }
            //}

            progress.value += step
            //if (BackupEntry.Name.FAVOURITES in checkedItems) {
            backup.getEntry(BackupEntry.Name.FAVOURITES)?.let {
                result += repository.restoreFavourites(it)
            }
            //}

            progress.value += step
            //if (BackupEntry.Name.BOOKMARKS in checkedItems) {
            backup.getEntry(BackupEntry.Name.BOOKMARKS)?.let {
                result += repository.restoreBookmarks(it)
            }
            //}

            progress.value += step
            //if (BackupEntry.Name.SOURCES in checkedItems) {
            backup.getEntry(BackupEntry.Name.SOURCES)?.let {
                result += repository.restoreSources(it)
            }
            //}

            progress.value = 1f
            backup.cleanupAsync()
            _events.send(Event.RestoreDone)
        }
    }

    /**
     * Check for inconsistent user selection
     * Favorites cannot be restored without categories
     */
    private fun MutableMap<BackupEntry.Name, BackupEntryModel>.validate() {
        val favorites = this[BackupEntry.Name.FAVOURITES] ?: return
        val categories = this[BackupEntry.Name.CATEGORIES]
        if (categories?.isChecked == true) {
            if (!favorites.isEnabled) {
                this[BackupEntry.Name.FAVOURITES] = favorites.copy(isEnabled = true)
            }
        } else {
            if (favorites.isEnabled) {
                this[BackupEntry.Name.FAVOURITES] =
                    favorites.copy(isEnabled = false, isChecked = false)
            }
        }
    }

    private fun setError(error: Any?, canRestore: Boolean) {
        mutableState.update {
            it.copy(
                error = error,
                canRestore = canRestore,
            )
        }
    }

    @Immutable
    data class State(
        val error: Any? = null,
        val canRestore: Boolean = false,
        val backupDate: Date? = null,
    )

    sealed interface Event {
        data object InternalError : Event
        data object RestoreDone : Event
    }

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(uri: String): RestoreBackupScreenModel
    }
}

private data class MissingRestoreComponents(
    val uri: Uri,
    val sources: List<String>,
    val trackers: List<String>,
)

private data class InvalidRestore(
    val uri: Uri? = null,
    val message: String,
)