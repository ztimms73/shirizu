package org.xtimms.shirizu.data

import android.content.Context
import android.os.StatFs
import androidx.annotation.WorkerThread
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import okhttp3.Cache
import org.koitharu.kotatsu.parsers.util.mapToSet
import org.xtimms.shirizu.core.cache.CacheDir
import org.xtimms.shirizu.utils.system.computeSize
import java.io.File
import javax.inject.Inject

private const val DIR_NAME = "manga"
private const val NOMEDIA = ".nomedia"
private const val CACHE_DISK_PERCENTAGE = 0.02
private const val CACHE_SIZE_MIN: Long = 10 * 1024 * 1024 // 10MB
const val CACHE_SIZE_MAX: Long = 250 * 1024 * 1024 // 250MB

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

    suspend fun computeCacheSize(cache: CacheDir) = withContext(Dispatchers.IO) {
        getCacheDirs(cache.dir).sumOf { it.computeSize() }
    }

    suspend fun computeCacheSize() = withContext(Dispatchers.IO) {
        getCacheDirs().sumOf { it.computeSize() }
    }

    suspend fun computeAvailableSize() = runInterruptible(Dispatchers.IO) {
        getAvailableStorageDirs().mapToSet { it.freeSpace }.sum()
    }

    fun getAvailableStorageSpace(file: File): Long {
        return try {
            val stat = StatFs(file.absolutePath)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (_: Exception) {
            -1L
        }
    }

    suspend fun clearCache(cache: CacheDir) = runInterruptible(Dispatchers.IO) {
        getCacheDirs(cache.dir).forEach { it.deleteRecursively() }
    }

    suspend fun getReadableDirs(): List<File> = runInterruptible(Dispatchers.IO) {
        getConfiguredStorageDirs()
            .filter { it.isReadable() }
    }

    suspend fun getWriteableDirs(): List<File> = runInterruptible(Dispatchers.IO) {
        getConfiguredStorageDirs()
            .filter { it.isWriteable() }
    }

    suspend fun getDefaultWriteableDir(): File? = runInterruptible(Dispatchers.IO) {
        val preferredDir = context.filesDir?.takeIf { it.isWriteable() }
        preferredDir ?: getFallbackStorageDir()?.takeIf { it.isWriteable() }
    }

    @WorkerThread
    private fun getConfiguredStorageDirs(): MutableSet<File> {
        val set = getAvailableStorageDirs()
        set.addAll(setOf(context.filesDir))
        return set
    }

    @WorkerThread
    private fun getAvailableStorageDirs(): MutableSet<File> {
        val result = LinkedHashSet<File>()
        result += File(context.filesDir, DIR_NAME)
        context.getExternalFilesDirs(DIR_NAME).filterNotNullTo(result)
        result.retainAll { it.exists() || it.mkdirs() }
        return result
    }

    @WorkerThread
    private fun getFallbackStorageDir(): File? {
        return context.getExternalFilesDir(DIR_NAME) ?: File(context.filesDir, DIR_NAME).takeIf {
            it.exists() || it.mkdirs()
        }
    }

    @WorkerThread
    private fun getCacheDirs(subDir: String): MutableSet<File> {
        val result = LinkedHashSet<File>()
        result += File(context.cacheDir, subDir)
        context.externalCacheDirs.mapNotNullTo(result) {
            File(it ?: return@mapNotNullTo null, subDir)
        }
        return result
    }

    @WorkerThread
    private fun getCacheDirs(): MutableSet<File> {
        val result = LinkedHashSet<File>()
        result += context.cacheDir
        context.externalCacheDirs.filterNotNullTo(result)
        return result
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

    private fun File.isReadable() = runCatching {
        canRead()
    }.getOrDefault(false)

    private fun File.isWriteable() = runCatching {
        canWrite()
    }.getOrDefault(false)
}