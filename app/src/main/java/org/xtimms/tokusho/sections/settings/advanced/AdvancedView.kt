package org.xtimms.tokusho.sections.settings.advanced

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.profileinstaller.ProfileVerifier
import kotlinx.coroutines.guava.await
import org.xtimms.tokusho.BuildConfig
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceItem
import org.xtimms.tokusho.core.components.PreferenceSubtitle
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.utils.WebViewUtil
import org.xtimms.tokusho.utils.lang.toDateTimestampString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

const val ADVANCED_DESTINATION = "advanced"

@Composable
fun AdvancedView(
    navigateBack: () -> Unit,
) {

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.advanced),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
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
                getProfileVerifierPreference()
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.webview_version),
                    description = getWebViewVersion()
                )
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
private fun getProfileVerifierPreference() {
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