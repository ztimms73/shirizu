package org.xtimms.tokusho.data

import android.content.Context
import android.os.StatFs
import androidx.annotation.WorkerThread
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import java.io.File
import javax.inject.Inject

private const val DIR_NAME = "manga"
private const val NOMEDIA = ".nomedia"
private const val CACHE_DISK_PERCENTAGE = 0.02
private const val CACHE_SIZE_MIN: Long = 10 * 1024 * 1024 // 10MB
private const val CACHE_SIZE_MAX: Long = 250 * 1024 * 1024 // 250MB

@Reusable
class LocalStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @WorkerThread
    fun createHttpCache(): Cache {
        val directory = File(context.externalCacheDir ?: context.cacheDir, "http")
        directory.mkdirs()
        val maxSize = calculateDiskCacheSize(directory)
        return Cache(directory, maxSize)
    }

    private fun calculateDiskCacheSize(cacheDirectory: File): Long {
        return try {
            val cacheDir = StatFs(cacheDirectory.absolutePath)
            val size = CACHE_DISK_PERCENTAGE * cacheDir.blockCountLong * cacheDir.blockSizeLong
            return size.toLong().coerceIn(CACHE_SIZE_MIN, CACHE_SIZE_MAX)
        } catch (_: Exception) {
            CACHE_SIZE_MIN
        }
    }

}