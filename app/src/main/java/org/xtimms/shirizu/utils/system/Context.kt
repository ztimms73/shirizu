package org.xtimms.shirizu.utils.system

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.work.WorkManager
import org.xtimms.shirizu.R
import org.xtimms.shirizu.utils.DeviceUtil
import org.xtimms.shirizu.utils.lang.truncateCenter
import java.io.File

val Context.activityManager: ActivityManager?
    get() = getSystemService(ACTIVITY_SERVICE) as? ActivityManager

val Context.workManager: WorkManager
    get() = WorkManager.getInstance(this)

fun Context.copyToClipboard(label: String, content: String) {
    if (content.isBlank()) return

    try {
        val clipboard = getSystemService<ClipboardManager>()!!
        clipboard.setPrimaryClip(ClipData.newPlainText(label, content))

        // Android 13 and higher shows a visual confirmation of copied contents
        // https://developer.android.com/about/versions/13/features/copy-paste
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            toast(resources.getString(R.string.copied_to_clipboard, content.truncateCenter(50)))
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        toast(R.string.clipboard_copy_error)
    }
}

fun Context.createFileInCacheDir(name: String): File {
    val file = File(externalCacheDir, name)
    if (file.exists()) {
        file.delete()
    }
    file.createNewFile()

    return file
}

fun Context.isLowRamDevice(): Boolean {
    return activityManager?.isLowRamDevice ?: false
}

fun Context.openInBrowser(url: String, forceDefaultBrowser: Boolean = false) {
    this.openInBrowser(url.toUri(), forceDefaultBrowser)
}

fun Context.openInBrowser(uri: Uri, forceDefaultBrowser: Boolean = false) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            if (forceDefaultBrowser) {
                defaultBrowserPackageName()?.let { setPackage(it) }
            }
        }
        startActivity(intent)
    } catch (e: Exception) {
        toast(e.getDisplayMessage(resources))
    }
}

private fun Context.defaultBrowserPackageName(): String? {
    val browserIntent = Intent(Intent.ACTION_VIEW, "http://".toUri())
    val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.resolveActivity(
            browserIntent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
        )
    } else {
        packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
    }
    return resolveInfo
        ?.activityInfo?.packageName
        ?.takeUnless { it in DeviceUtil.invalidDefaultBrowsers }
}