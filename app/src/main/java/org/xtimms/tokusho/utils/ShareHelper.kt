package org.xtimms.tokusho.utils

import android.content.Context
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import org.xtimms.tokusho.BuildConfig
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.logs.FileLogger

private const val TYPE_TEXT = "text/plain"

class ShareHelper(private val context: Context) {

    fun shareLogs(loggers: Collection<FileLogger>) {
        val intentBuilder = ShareCompat.IntentBuilder(context)
            .setType(TYPE_TEXT)
        var hasLogs = false
        for (logger in loggers) {
            val logFile = logger.file
            if (!logFile.exists()) {
                continue
            }
            val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.files", logFile)
            intentBuilder.addStream(uri)
            hasLogs = true
        }
        if (hasLogs) {
            intentBuilder.setChooserTitle(R.string.share_logs)
            intentBuilder.startChooser()
        } else {
            Toast.makeText(context, R.string.nothing_here, Toast.LENGTH_SHORT).show()
        }
    }

}