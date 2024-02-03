package org.xtimms.tokusho.utils.system

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import java.io.File

val Context.activityManager: ActivityManager?
    get() = getSystemService(ACTIVITY_SERVICE) as? ActivityManager

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