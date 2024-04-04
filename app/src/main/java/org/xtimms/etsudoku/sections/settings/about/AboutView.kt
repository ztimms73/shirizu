package org.xtimms.etsudoku.sections.settings.about

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.UpdateDisabled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import org.xtimms.etsudoku.App
import org.xtimms.etsudoku.App.Companion.packageInfo
import org.xtimms.etsudoku.R
import org.xtimms.etsudoku.core.components.PreferenceItem
import org.xtimms.etsudoku.core.components.PreferenceSwitchWithDivider
import org.xtimms.etsudoku.core.components.ScaffoldWithTopAppBar
import org.xtimms.etsudoku.core.prefs.AUTO_UPDATE
import org.xtimms.etsudoku.core.prefs.AppSettings
import org.xtimms.etsudoku.utils.system.toast

const val ABOUT_DESTINATION = "about"

private const val repoUrl = "https://git.kotatsu.app/Xtimms/Etsudoku"
const val weblate = "https://hosted.weblate.org/engage/etsudoku/"

@Composable
fun AboutView(
    navigateBack: () -> Unit,
    navigateToLicensesPage: () -> Unit,
    navigateToUpdatePage: () -> Unit,
) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isAutoUpdateEnabled by remember { mutableStateOf(AppSettings.isAutoUpdateEnabled()) }

    val info = App.getVersionReport()
    val versionName = packageInfo.versionName

    var versionClicks by remember { mutableIntStateOf(0) }

    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.about),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                PreferenceItem(
                    title = stringResource(R.string.readme),
                    description = stringResource(R.string.readme_desc),
                    icon = Icons.Outlined.Description,
                ) { openUrl(repoUrl) }
            }
            item {
                PreferenceSwitchWithDivider(
                    title = stringResource(R.string.auto_update),
                    description = stringResource(R.string.check_for_updates_desc),
                    icon = if (isAutoUpdateEnabled) Icons.Outlined.Update else Icons.Outlined.UpdateDisabled,
                    isChecked = isAutoUpdateEnabled,
                    onClick = navigateToUpdatePage,
                    onChecked = {
                        isAutoUpdateEnabled = !isAutoUpdateEnabled
                        AppSettings.updateValue(AUTO_UPDATE, isAutoUpdateEnabled)
                    }
                )
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.version),
                    description = versionName,
                    icon = Icons.Outlined.Info,
                    onLongClick = {
                        clipboardManager.setText(AnnotatedString(info))
                        context.toast(R.string.info_copied)
                    }
                ) {
                    if (versionClicks >= 7) {
                        context.toast("✧◝(⁰▿⁰)◜✧")
                        versionClicks = 0
                    } else versionClicks++
                }
            }
            item {
                PreferenceItem(
                    title = stringResource(id = R.string.open_source_licenses),
                    icon = Icons.Outlined.DeveloperBoard
                ) {
                    navigateToLicensesPage()
                }
            }
        }
    }

}