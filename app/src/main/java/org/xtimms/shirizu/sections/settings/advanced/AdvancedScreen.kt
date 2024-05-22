package org.xtimms.shirizu.sections.settings.advanced

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.PrintDisabled
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.ReportOff
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.profileinstaller.ProfileVerifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.guava.await
import org.xtimms.shirizu.BuildConfig
import org.xtimms.shirizu.LocalLoggers
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferenceItem
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitch
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.prefs.ACRA
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.LOGGING
import org.xtimms.shirizu.utils.DeviceUtil
import org.xtimms.shirizu.utils.ShareHelper
import org.xtimms.shirizu.utils.WebViewUtil
import org.xtimms.shirizu.utils.lang.Screen
import org.xtimms.shirizu.utils.lang.toDateTimestampString
import org.xtimms.shirizu.utils.system.toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object AdvancedScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val loggers = LocalLoggers.current

        var isAcraEnabled by remember {
            mutableStateOf(AppSettings.isACRAEnabled())
        }

        var isLoggingEnabled by remember {
            mutableStateOf(AppSettings.isLoggingEnabled())
        }

        ScaffoldWithTopAppBar(
            title = stringResource(R.string.advanced),
            navigateBack = navigator::pop
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
            ) {
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.send_crash_reports),
                        description = stringResource(id = R.string.send_crash_reports_desc),
                        icon = if (isAcraEnabled) Icons.Outlined.Report else Icons.Outlined.ReportOff,
                        isChecked = isAcraEnabled,
                        onClick = {
                            isAcraEnabled = !isAcraEnabled
                            AppSettings.updateValue(ACRA, isAcraEnabled)
                            context.toast(R.string.restart_required)
                        }
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.enable_logging),
                        description = stringResource(id = R.string.enable_logging_desc),
                        icon = if (isLoggingEnabled) Icons.Outlined.Print else Icons.Outlined.PrintDisabled,
                        isChecked = isLoggingEnabled,
                        onClick = {
                            isLoggingEnabled = !isLoggingEnabled
                            AppSettings.updateValue(LOGGING, isLoggingEnabled)
                        }
                    )
                }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.share_logs),
                        icon = Icons.Outlined.Share,
                        enabled = isLoggingEnabled,
                        onClick = {
                            ShareHelper(context).shareLogs(loggers)
                        }
                    )
                }
                if (BuildConfig.DEBUG) {
                    item {
                        PreferenceSubtitle(text = stringResource(id = R.string.debug_info))
                    }
                    item {
                        PreferenceItem(
                            title = stringResource(id = R.string.worker_info),
                            onClick = {
                                val intent = Intent()
                                intent.component = ComponentName(
                                    context,
                                    "org.koitharu.workinspector.WorkInspectorActivity"
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                }
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.app_info))
                }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.app_version),
                        description = getVersionName(false)
                    )
                }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.build_time),
                        description = getFormattedBuildTime()
                    )
                }
                item {
                    GetProfileVerifierPreference()
                }
                item {
                    PreferenceItem(
                        title = stringResource(id = R.string.webview_version),
                        description = getWebViewVersion()
                    )
                }
                item {
                    PreferenceSubtitle(text = stringResource(id = R.string.device_info))
                }
                item {
                    PreferenceItem(
                        title = "Model",
                        description = "${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})"
                    )
                }
                if (DeviceUtil.oneUiVersion != null) {
                    item {
                        PreferenceItem(
                            title = "OneUI version",
                            description = "${DeviceUtil.oneUiVersion}"
                        )
                    }
                }
                if (DeviceUtil.miuiMajorVersion != null) {
                    item {
                        PreferenceItem(
                            title = "MIUI version",
                            description = "${DeviceUtil.miuiMajorVersion}",
                        )
                    }
                }
                val androidVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Build.VERSION.RELEASE_OR_CODENAME
                } else {
                    Build.VERSION.RELEASE
                }
                item {
                    PreferenceItem(
                        title = "Android version",
                        description = "$androidVersion (${Build.DISPLAY})"
                    )
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun getWebViewVersion(): String {
    return WebViewUtil.getVersion(LocalContext.current)
}

@Composable
private fun GetProfileVerifierPreference() {
    val status by produceState(initialValue = "-") {
        val result = ProfileVerifier.getCompilationStatusAsync().await().profileInstallResultCode
        value = when (result) {
            ProfileVerifier.CompilationStatus.RESULT_CODE_NO_PROFILE -> "No profile installed"
            ProfileVerifier.CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE -> "Compiled"
            ProfileVerifier.CompilationStatus.RESULT_CODE_COMPILED_WITH_PROFILE_NON_MATCHING ->
                "Compiled non-matching"

            ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_CACHE_FILE_EXISTS_BUT_CANNOT_BE_READ,
            ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE,
            ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_PACKAGE_NAME_DOES_NOT_EXIST,
            -> "Error $result"

            ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_UNSUPPORTED_API_VERSION -> "Not supported"
            ProfileVerifier.CompilationStatus.RESULT_CODE_PROFILE_ENQUEUED_FOR_COMPILATION -> "Pending compilation"
            else -> "Unknown code $result"
        }
    }
    return PreferenceItem(
        title = "Profile compilation status",
        description = status,
    )
}

fun getVersionName(withBuildDate: Boolean): String {
    return when {
        BuildConfig.DEBUG -> {
            "Debug ${BuildConfig.COMMIT_SHA}".let {
                if (withBuildDate) {
                    "$it (${getFormattedBuildTime()})"
                } else {
                    it
                }
            }
        }

        else -> {
            "Stable ${BuildConfig.VERSION_NAME}".let {
                if (withBuildDate) {
                    "$it (${getFormattedBuildTime()})"
                } else {
                    it
                }
            }
        }
    }
}

internal fun getFormattedBuildTime(): String {
    return try {
        val inputDf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US)
        inputDf.timeZone = TimeZone.getTimeZone("UTC")
        val buildTime = inputDf.parse(BuildConfig.BUILD_TIME)

        val outputDf = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
            Locale.getDefault(),
        )
        outputDf.timeZone = TimeZone.getDefault()

        buildTime!!.toDateTimestampString(DateFormat.getDateTimeInstance())
    } catch (e: Exception) {
        BuildConfig.BUILD_TIME
    }
}