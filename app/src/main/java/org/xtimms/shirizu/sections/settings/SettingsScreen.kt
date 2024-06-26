package org.xtimms.shirizu.sections.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.BackIconButton
import org.xtimms.shirizu.core.components.PreferencesHintCard
import org.xtimms.shirizu.core.components.SettingItem
import org.xtimms.shirizu.core.components.SettingTitle
import org.xtimms.shirizu.core.components.SmallTopAppBar
import org.xtimms.shirizu.sections.settings.about.AboutScreen
import org.xtimms.shirizu.sections.settings.advanced.AdvancedScreen
import org.xtimms.shirizu.sections.settings.appearance.AppearanceScreen
import org.xtimms.shirizu.sections.settings.backup.BackupRestoreScreen
import org.xtimms.shirizu.sections.settings.network.NetworkScreen
import org.xtimms.shirizu.sections.settings.services.ServicesScreen
import org.xtimms.shirizu.sections.settings.shelf.ShelfSettingsScreen
import org.xtimms.shirizu.sections.settings.storage.StorageScreen
import org.xtimms.shirizu.sections.settings.storage.StorageScreenModel
import org.xtimms.shirizu.utils.FileSize
import org.xtimms.shirizu.utils.lang.Screen

@SuppressLint("BatteryLife")
@OptIn(ExperimentalMaterial3Api::class)
object SettingsScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        var showBatteryHint by remember {
            mutableStateOf(!pm.isIgnoringBatteryOptimizations(context.packageName))
        }

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val intent =
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
            }

        val isActivityAvailable: Boolean =
            if (Build.VERSION.SDK_INT < 33) context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            ).isNotEmpty()
            else context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_SYSTEM_ONLY.toLong())
            ).isNotEmpty()

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                showBatteryHint = !pm.isIgnoringBatteryOptimizations(context.packageName)
            }

        val screenModel = getScreenModel<StorageScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SmallTopAppBar(
                    titleText = stringResource(id = R.string.settings),
                    navigationIcon = {
                        BackIconButton(navigator::pop)
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            contentWindowInsets = WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal),
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            ) {
                item {
                    SettingTitle(text = stringResource(id = R.string.settings))
                }
                item {
                    AnimatedVisibility(
                        visible = showBatteryHint && isActivityAvailable,
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        PreferencesHintCard(
                            title = stringResource(R.string.disable_battery_optimization),
                            icon = Icons.Outlined.BatterySaver,
                            description = stringResource(R.string.disable_battery_optimization_summary),
                        ) {
                            launcher.launch(intent)
                            showBatteryHint =
                                !pm.isIgnoringBatteryOptimizations(context.packageName)
                        }
                    }
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.appearance),
                        description = stringResource(id = R.string.appearance_page),
                        icon = Icons.Outlined.Palette,
                        onClick = { navigator.push(AppearanceScreen) }
                    )
                }
                /*item {
                    SettingItem(
                        title = stringResource(id = R.string.manga_sources),
                        *//*description = if (enabled.value >= 0) stringResource(
                            id = R.string.enabled_d_of_d,
                            enabled.value,
                            total
                        ) else context.resources.getQuantityString(R.plurals.items, total, total),*//*
                        description = "TODO",
                        icon = Icons.Outlined.CollectionsBookmark,
                        onClick = {  }
                    )
                }*/
                item {
                    SettingItem(
                        title = stringResource(id = R.string.nav_shelf),
                        description = stringResource(id = R.string.shelf_page),
                        icon = Icons.Outlined.LocalLibrary,
                        onClick = { navigator.push(ShelfSettingsScreen) }
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.services),
                        description = stringResource(id = R.string.services_page),
                        icon = Icons.Outlined.Extension,
                        onClick = { navigator.push(ServicesScreen) }
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.backup_and_restore),
                        description = "TODO",
                        icon = Icons.Outlined.SettingsBackupRestore,
                        onClick = { navigator.push(BackupRestoreScreen) }
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.network),
                        description = stringResource(id = R.string.network_page),
                        icon = Icons.Outlined.Wifi,
                        onClick = { navigator.push(NetworkScreen) }
                    )
                }
                item {
                    val allCaches = state.httpCacheSize +
                            state.pagesCache +
                            state.thumbnailsCache
                    val desc = buildString {
                        append((allCaches / state.availableSpace) * 100)
                        append(context.getString(R.string.space_used))
                        append(" - ")
                        append(
                            FileSize.BYTES.freeFormat(
                                context,
                                (state.availableSpace -
                                        state.httpCacheSize -
                                        state.pagesCache -
                                        state.thumbnailsCache).toFloat()
                            )
                        )
                    }
                    SettingItem(
                        title = stringResource(id = R.string.storage),
                        description = desc,
                        icon = Icons.Outlined.Storage,
                        onClick = { navigator.push(StorageScreen) }
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.advanced),
                        description = stringResource(id = R.string.advanced_page),
                        icon = Icons.Outlined.Code,
                        onClick = { navigator.push(AdvancedScreen) }
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.about),
                        description = stringResource(id = R.string.about_page),
                        icon = Icons.Outlined.Info,
                        onClick = { navigator.push(AboutScreen) }
                    )
                }
            }
        }
    }
}