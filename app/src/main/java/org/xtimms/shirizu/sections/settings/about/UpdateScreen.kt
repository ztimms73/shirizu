package org.xtimms.shirizu.sections.settings.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferenceInfo
import org.xtimms.shirizu.core.components.PreferenceSingleChoiceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitchWithContainer
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.prefs.AUTO_UPDATE
import org.xtimms.shirizu.core.prefs.AppSettings.updateBoolean
import org.xtimms.shirizu.core.prefs.AppSettings.updateInt
import org.xtimms.shirizu.core.prefs.PRE_RELEASE
import org.xtimms.shirizu.core.prefs.STABLE
import org.xtimms.shirizu.core.prefs.UPDATE_CHANNEL
import org.xtimms.shirizu.core.ui.dialogs.UpdateDialog
import org.xtimms.shirizu.core.updates.Updater
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.booleanState
import org.xtimms.shirizu.utils.lang.intState
import org.xtimms.shirizu.utils.system.suspendToast

object UpdateScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var autoUpdate by AUTO_UPDATE.booleanState
        var updateChannel by UPDATE_CHANNEL.intState

        var latestRelease by remember { mutableStateOf(Updater.LatestRelease()) }
        var showUpdateDialog by remember { mutableStateOf(false) }

        ScaffoldWithTopAppBar(
            title = stringResource(R.string.auto_update),
            navigateBack = navigator::pop
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            ) {
                item {
                    PreferenceSwitchWithContainer(
                        title = stringResource(id = R.string.enable_auto_update),
                        icon = null,
                        isChecked = autoUpdate
                    ) {
                        autoUpdate = !autoUpdate
                        AUTO_UPDATE.updateBoolean(autoUpdate)
                    }
                }
                item {
                    PreferenceSubtitle(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        text = stringResource(id = R.string.update_channel)
                    )
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(id = R.string.stable_channel),
                        selected = updateChannel == STABLE,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        updateChannel = STABLE
                        UPDATE_CHANNEL.updateInt(updateChannel)
                    }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(id = R.string.pre_release_channel),
                        selected = updateChannel == PRE_RELEASE,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        updateChannel = PRE_RELEASE
                        UPDATE_CHANNEL.updateInt(updateChannel)
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
                                id = R.string.check_for_updates
                            ),
                            icon = Icons.Outlined.Update,
                            isLoading = isLoading
                        ) {
                            if (!isLoading)
                                scope.launch {
                                    runCatching {
                                        isLoading = true
                                        withContext(Dispatchers.IO) {
                                            Updater.checkForUpdate(context)?.let {
                                                latestRelease = it
                                                showUpdateDialog = true
                                            }
                                                ?: context.suspendToast(R.string.app_up_to_date)
                                        }
                                        isLoading = false
                                    }
                                        .onFailure {
                                            it.printStackTrace()
                                            context.suspendToast(R.string.app_update_failed)
                                            isLoading = false
                                        }
                                }
                        }
                    }
                    HorizontalDivider()
                }
                item {
                    PreferenceInfo(
                        modifier = Modifier
                            .padding(horizontal = 4.dp),
                        text = stringResource(id = R.string.update_channel_desc)
                    )
                }
            }
        }
        if (showUpdateDialog)
            UpdateDialog(onDismissRequest = { showUpdateDialog = false }, latestRelease = latestRelease)
    }
}

@Composable
fun ProgressIndicatorButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        if (isLoading)
            Box(modifier = Modifier.size(18.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center),
                    strokeWidth = 3.dp
                )
            }
        else Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}