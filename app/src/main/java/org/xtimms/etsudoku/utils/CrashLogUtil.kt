package org.xtimms.etsudoku.utils

import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xtimms.etsudoku.BuildConfig
import org.xtimms.etsudoku.utils.lang.withNonCancellableContext
import org.xtimms.etsudoku.utils.system.createFileInCacheDir
import org.xtimms.etsudoku.utils.system.getUriCompat
import org.xtimms.etsudoku.utils.system.toShareIntent
import org.xtimms.etsudoku.utils.system.toast

class CrashLogUtil(
    private val context: Context,
) {

    suspend fun dumpLogs() = withNonCancellableContext {
        try {
            val file = context.createFileInCacheDir("etsudoku_crash_logs.txt")

            file.appendText(getDebugInfo() + "\n\n")

            Runtime.getRuntime().exec("logcat *:E -d -f ${file.absolutePath}").waitFor()

            val uri = file.getUriCompat(context)
            context.startActivity(uri.toShareIntent(context, "text/plain"))
        } catch (e: Throwable) {
            withContext(Dispatchers.IO) { context.toast("Failed to get logs") }
        }
    }

    fun getDebugInfo(): String {
        return """
            App version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
            Android version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT}; build ${Build.DISPLAY})
            Device brand: ${Build.BRAND}
            Device manufacturer: ${Build.MANUFACTURER}
            Device name: ${Build.DEVICE} (${Build.PRODUCT})
            Device model: ${Build.MODEL}
        """.trimIndent()
    }
}