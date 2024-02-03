package org.xtimms.tokusho.sections.settings.appearance

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.xtimms.tokusho.MainActivity
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceSingleChoiceItem
import org.xtimms.tokusho.core.components.PreferencesHintCard
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.AppSettings.getLanguageConfiguration
import org.xtimms.tokusho.core.prefs.LANGUAGE
import org.xtimms.tokusho.core.prefs.SYSTEM_DEFAULT
import org.xtimms.tokusho.sections.settings.about.weblate
import org.xtimms.tokusho.ui.theme.TokushoTheme
import org.xtimms.tokusho.utils.system.getLanguageDesc
import org.xtimms.tokusho.utils.system.languageMap

const val LANGUAGES_DESTINATION = "languages"

@Composable
fun LanguagesView(
    navigateBack: () -> Unit
) {
    var language by remember { mutableStateOf(AppSettings.getLanguageNumber()) }
    val context = LocalContext.current
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Intent(android.provider.Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }
    } else {
        Intent()
    }

    val isSystemLocaleSettingsAvailable =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_ALL
            ).isNotEmpty()
        } else {
            false
        }
    LanguageViewImpl(
        navigateBack = navigateBack,
        languageMap = languageMap,
        isSystemLocaleSettingsAvailable = isSystemLocaleSettingsAvailable,
        onNavigateToSystemLocaleSettings = {
            if (isSystemLocaleSettingsAvailable) {
                context.startActivity(intent)
            }
        },
        selectedLanguage = language,
    ) {
        language = it
        AppSettings.encodeInt(LANGUAGE, language)
        MainActivity.setLanguage(getLanguageConfiguration())
    }
}

@Composable
private fun LanguageViewImpl(
    navigateBack: () -> Unit = {},
    languageMap: Map<Int, String>,
    isSystemLocaleSettingsAvailable: Boolean = false,
    onNavigateToSystemLocaleSettings: () -> Unit,
    selectedLanguage: Int,
    onLanguageSelected: (Int) -> Unit = {}
) {

    val uriHandler = LocalUriHandler.current

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.language),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item {
                PreferencesHintCard(
                    title = stringResource(R.string.translate),
                    description = stringResource(R.string.translate_desc),
                    icon = Icons.Outlined.Translate,
                ) { uriHandler.openUri(weblate) }
            }
            item {
                PreferenceSingleChoiceItem(
                    text = stringResource(R.string.follow_system),
                    selected = selectedLanguage == SYSTEM_DEFAULT,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 18.dp)
                ) { onLanguageSelected(SYSTEM_DEFAULT) }
            }
            for (languageData in languageMap) {
                item {
                    PreferenceSingleChoiceItem(
                        text = getLanguageDesc(languageData.key),
                        selected = selectedLanguage == languageData.key,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 18.dp)
                    ) { onLanguageSelected(languageData.key) }
                }
            }
            if (isSystemLocaleSettingsAvailable) {
                item {
                    HorizontalDivider()
                    Surface(
                        modifier = Modifier.clickable(
                            onClick = onNavigateToSystemLocaleSettings
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PaddingValues(horizontal = 12.dp, vertical = 18.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.system_settings),
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LanguagePagePreview() {
    var language by remember {
        mutableIntStateOf(1)
    }
    val map = buildMap<Int, String> {
        repeat(38) {
            put(it + 1, "")
        }
    }
    TokushoTheme {
        LanguageViewImpl(
            languageMap = map,
            isSystemLocaleSettingsAvailable = true,
            onNavigateToSystemLocaleSettings = { /*TODO*/ },
            selectedLanguage = language
        ) {
            language = it
        }
    }
}