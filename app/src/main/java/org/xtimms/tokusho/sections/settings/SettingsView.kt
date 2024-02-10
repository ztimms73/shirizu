package org.xtimms.tokusho.sections.settings

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceSubtitle
import org.xtimms.tokusho.core.components.PreferencesHintCard
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.components.SettingItem
import org.xtimms.tokusho.sections.settings.storage.StorageViewModel
import org.xtimms.tokusho.utils.FileSize

const val SETTINGS_DESTINATION = "settings"

@SuppressLint("BatteryLife")
@Composable
fun SettingsView(
    navigateBack: () -> Unit,
    navigateToAppearance: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToAdvanced: () -> Unit,
    navigateToStorage: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: StorageViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var showBatteryHint by remember {
        mutableStateOf(!pm.isIgnoringBatteryOptimizations(context.packageName))
    }

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

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.settings),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
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
                    onClick = navigateToAppearance
                )
            }
            item {
                val allCaches = uiState.httpCacheSize +
                        uiState.pagesCache +
                        uiState.thumbnailsCache
                val desc = buildString {
                    append((allCaches / uiState.availableSpace) * 100)
                    append(context.getString(R.string.space_used))
                    append(" - ")
                    append(
                        FileSize.BYTES.freeFormat(
                            context,
                            uiState.availableSpace -
                                    uiState.httpCacheSize -
                                    uiState.pagesCache -
                                    uiState.thumbnailsCache
                        )
                    )
                }
                SettingItem(
                    title = stringResource(id = R.string.storage),
                    description = desc,
                    icon = Icons.Outlined.Storage,
                    onClick = navigateToStorage
                )
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.advanced),
                    description = stringResource(id = R.string.advanced_page),
                    icon = Icons.Outlined.Code,
                    onClick = navigateToAdvanced
                )
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.about),
                    description = stringResource(id = R.string.about_page),
                    icon = Icons.Outlined.Info,
                    onClick = navigateToAbout
                )
            }
        }
    }
}