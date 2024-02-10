package org.xtimms.tokusho.utils.system

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import org.xtimms.tokusho.BuildConfig
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.walk

fun File.getUriCompat(context: Context): Uri {
    return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", this)
}

fun Context.getFileProvider() = "$packageName.provider"

suspend fun File.computeSize(): Long = runInterruptible(Dispatchers.IO) {
    walkCompat().sumOf { it.length() }
}

@OptIn(ExperimentalPathApi::class)
fun File.walkCompat() =
    // Use lazy loading on Android 8.0 and later
    toPath().walk().map { it.toFile() }
