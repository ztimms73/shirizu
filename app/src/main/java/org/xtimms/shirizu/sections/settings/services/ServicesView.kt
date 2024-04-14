package org.xtimms.shirizu.sections.settings.services

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.components.PreferenceSubtitle
import org.xtimms.shirizu.core.components.PreferenceSwitch
import org.xtimms.shirizu.core.components.PreferenceSwitchWithDivider
import org.xtimms.shirizu.core.components.ScaffoldWithTopAppBar
import org.xtimms.shirizu.core.components.icons.Creation
import org.xtimms.shirizu.core.prefs.AppSettings
import org.xtimms.shirizu.core.prefs.READING_TIME
import org.xtimms.shirizu.core.prefs.RELATED
import org.xtimms.shirizu.core.prefs.STATISTICS
import org.xtimms.shirizu.core.prefs.SUGGESTIONS

const val SERVICES_DESTINATION = "services"

@Composable
fun ServicesView(
    navigateBack: () -> Unit,
    navigateToSuggestionsSettings: () -> Unit,
    navigateToStatistics: () -> Unit
) {

    var isSuggestionsEnabled by remember { mutableStateOf(AppSettings.isSuggestionsEnabled()) }
    var isRelatedEnabled by remember { mutableStateOf(AppSettings.isRelatedMangaEnabled()) }
    var isStatisticsEnabled by remember { mutableStateOf(AppSettings.isStatisticsEnabled()) }
    var isReadingTimeEstimationEnabled by remember { mutableStateOf(AppSettings.isReadingTimeEstimationEnabled()) }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.services),
        navigateBack = navigateBack
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.manga))
            }
            item {
                PreferenceSwitchWithDivider(
                    title = stringResource(R.string.suggestions),
                    description = stringResource(id = R.string.suggestions_summary),
                    icon = Icons.Outlined.Creation,
                    isChecked = isSuggestionsEnabled,
                    onClick = navigateToSuggestionsSettings,
                    onChecked = {
                        isSuggestionsEnabled = !isSuggestionsEnabled
                        AppSettings.updateValue(SUGGESTIONS, isSuggestionsEnabled)
                    }
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.related_manga),
                    description = stringResource(id = R.string.related_manga_summary),
                    icon = Icons.Outlined.CollectionsBookmark,
                    isChecked = isRelatedEnabled,
                    onClick = {
                        isRelatedEnabled = !isRelatedEnabled
                        AppSettings.updateValue(RELATED, isRelatedEnabled)
                    }
                )
            }
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.statistics))
            }
            item {
                PreferenceSwitchWithDivider(
                    title = stringResource(R.string.recording_statistics),
                    description = if (isStatisticsEnabled) stringResource(id = R.string.enabled) else stringResource(
                        id = R.string.disabled
                    ),
                    icon = Icons.Outlined.QueryStats,
                    isChecked = isStatisticsEnabled,
                    onClick = navigateToStatistics,
                    onChecked = {
                        isStatisticsEnabled = !isStatisticsEnabled
                        AppSettings.updateValue(STATISTICS, isStatisticsEnabled)
                    }
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.show_estimated_read_time),
                    description = stringResource(id = R.string.show_estimated_read_time_desc),
                    icon = Icons.Outlined.Timelapse,
                    isChecked = isReadingTimeEstimationEnabled,
                    onClick = {
                        isReadingTimeEstimationEnabled = !isReadingTimeEstimationEnabled
                        AppSettings.updateValue(READING_TIME, isReadingTimeEstimationEnabled)
                    }
                )
            }
        }
    }

}