package org.xtimms.shirizu.crash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.screens.InfoScreen
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.CrashLogUtil

@Composable
fun CrashScreen(
    exception: Throwable?,
    onRestartClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    InfoScreen(
        icon = Icons.Outlined.BugReport,
        headingText = stringResource(R.string.crash_screen_title),
        subtitleText = stringResource(R.string.crash_screen_description, stringResource(R.string.app_name)),
        acceptText = stringResource(R.string.pref_dump_crash_logs),
        onAcceptClick = {
            scope.launch {
                CrashLogUtil(context).dumpLogs()
            }
        },
        rejectText = stringResource(R.string.crash_screen_restart_application),
        onRejectClick = onRestartClick,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Text(
                text = exception.toString(),
                modifier = Modifier
                    .padding(all = 8.dp),
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CrashScreenPreview() {
    ShirizuTheme {
        CrashScreen(exception = RuntimeException("Dummy")) {}
    }
}