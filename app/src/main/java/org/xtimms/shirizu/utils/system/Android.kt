package org.xtimms.shirizu.utils.system

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import androidx.work.CoroutineWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import java.io.File
import java.io.IOException

fun <I> ActivityResultLauncher<I>.tryLaunch(
    input: I,
    options: ActivityOptionsCompat? = null,
): Boolean = runCatching {
    launch(input, options)
}.onFailure { e ->
    e.printStackTrace()
}.isSuccess

fun String.toUriOrNull() = if (isEmpty()) null else Uri.parse(this)

val Context.powerManager: PowerManager?
    get() = getSystemService(POWER_SERVICE) as? PowerManager

suspend fun CoroutineWorker.trySetForeground(): Boolean = runCatchingCancellable {
    val info = getForegroundInfo()
    setForeground(info)
}.isSuccess

fun Context.checkNotificationPermission(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
} else {
    NotificationManagerCompat.from(this).areNotificationsEnabled()
}

@WorkerThread
suspend fun Bitmap.compressToPNG(output: File) = runInterruptible(Dispatchers.IO) {
    output.outputStream().use { os ->
        if (!compress(Bitmap.CompressFormat.PNG, 100, os)) {
            throw IOException("Failed to encode bitmap into PNG format")
        }
    }
}

val Context.ramAvailable: Long
    get() {
        val result = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(result)
        return result.availMem
    }

fun Context.ensureRamAtLeast(requiredSize: Long) {
    if (ramAvailable < requiredSize) {
        throw IllegalStateException("Not enough free memory")
    }
}

fun Context.isPowerSaveMode(): Boolean {
    return powerManager?.isPowerSaveMode == true
}

fun WebView.configureForParser(userAgentOverride: String?) = with(settings) {
    javaScriptEnabled = true
    domStorageEnabled = true
    mediaPlaybackRequiresUserGesture = false
    if (WebViewFeature.isFeatureSupported(WebViewFeature.MUTE_AUDIO)) {
        WebViewCompat.setAudioMuted(this@configureForParser, true)
    }
    databaseEnabled = true
    if (userAgentOverride != null) {
        userAgentString = userAgentOverride
    }
}