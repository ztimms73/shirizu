package org.xtimms.shirizu.sections.settings.services.suggestions

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.NoAdultContent
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferenceInfo
import org.xtimms.shirizu.core.components.PreferenceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitch
import org.xtimms.shirizu.core.components.PreferenceSwitchWithContainer
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.prefs.AppSettings.updateBoolean
import org.xtimms.shirizu.core.prefs.SUGGESTIONS
import org.xtimms.shirizu.core.prefs.SUGGESTIONS_NONMETERED
import org.xtimms.shirizu.core.prefs.SUGGESTIONS_NOTIFICATIONS
import org.xtimms.shirizu.core.prefs.SUGGESTIONS_NSFW
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.booleanState
import org.xtimms.shirizu.utils.system.toast

@OptIn(ExperimentalPermissionsApi::class)
object SuggestionsSettingsScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var suggestionsEnabled by SUGGESTIONS.booleanState
        var nonMeteredNetwork by SUGGESTIONS_NONMETERED.booleanState
        var notifications by SUGGESTIONS_NOTIFICATIONS.booleanState
        var nsfwSuggestions by SUGGESTIONS_NSFW.booleanState

        val enableSuggestionsNotifications = {
            notifications = !notifications
            SUGGESTIONS_NOTIFICATIONS.updateBoolean(notifications)
        }

        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(
                permission = Manifest.permission.POST_NOTIFICATIONS
            ) { b: Boolean ->
                if (b) {
                    enableSuggestionsNotifications()
                } else {
                    context.toast(R.string.permission_denied)
                }
            }
        } else null

        val checkPermission = {
            if (notificationPermission?.status == PermissionStatus.Granted) {
                enableSuggestionsNotifications()
            } else {
                notificationPermission?.launchPermissionRequest()
            }
        }

        ScaffoldWithTopAppBar(
            title = stringResource(R.string.suggestions),
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
                        title = stringResource(id = R.string.enable_suggestions),
                        isChecked = suggestionsEnabled
                    ) {
                        suggestionsEnabled = !suggestionsEnabled
                        SUGGESTIONS.updateBoolean(suggestionsEnabled)
                    }
                }
                item {
                    PreferenceSwitch(
                        enabled = suggestionsEnabled,
                        icon = Icons.Outlined.Wifi,
                        title = stringResource(id = R.string.only_on_wifi),
                        description = stringResource(id = R.string.only_on_wifi_desc),
                        isChecked = nonMeteredNetwork
                    ) {
                        nonMeteredNetwork = !nonMeteredNetwork
                        SUGGESTIONS_NONMETERED.updateBoolean(nonMeteredNetwork)
                    }
                }
                item {
                    PreferenceSwitch(
                        enabled = suggestionsEnabled,
                        icon = Icons.Outlined.Notifications,
                        title = stringResource(id = R.string.suggestions_notifications),
                        description = stringResource(id = R.string.suggestions_notifications_desc),
                        isChecked = notifications
                    ) {
                        checkPermission()
                    }
                }
                item{
                    PreferenceSubtitle(text = stringResource(id = R.string.advanced))
                }
                item {
                    PreferenceItem(
                        enabled = suggestionsEnabled,
                        title = stringResource(id = R.string.exclude_genres),
                        description = stringResource(id = R.string.exclude_genres_desc),
                        icon = Icons.Outlined.FilterAlt
                    )
                }
                item {
                    PreferenceSwitch(
                        enabled = suggestionsEnabled,
                        title = stringResource(id = R.string.do_not_suggest_nsfw_manga),
                        icon = Icons.Outlined.NoAdultContent,
                        isChecked = nsfwSuggestions
                    ) {
                        nsfwSuggestions = !nsfwSuggestions
                        SUGGESTIONS_NSFW.updateBoolean(nsfwSuggestions)
                    }
                }
                item {
                    HorizontalDivider()
                }
                item {
                    PreferenceInfo(text = stringResource(id = R.string.suggestions_info))
                }
            }
        }
    }
}