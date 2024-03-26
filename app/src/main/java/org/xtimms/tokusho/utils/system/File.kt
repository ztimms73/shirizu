package org.xtimms.tokusho.utils.system

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.xtimms.tokusho.BuildConfig
import org.xtimms.tokusho.utils.FileSequence
import java.io.File
import java.io.FileFilter
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.PathWalkOption
import kotlin.io.path.readAttributes
import kotlin.io.path.walk

fun File.subdir(name: String) = File(this, name).also {
    if (!it.exists()) it.mkdirs()
}

fun File.getUriCompat(context: Context): Uri {
    return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", this)
}

fun Context.getFileProvider() = "$packageName.provider"

suspend fun File.computeSize(): Long = runInterruptible(Dispatchers.IO) {
    walkCompat(includeDirectories = false).sumOf { it.length() }
}

@OptIn(ExperimentalPathApi::class)
fun File.walkCompat(includeDirectories: Boolean): Sequence<File> {
    // Use lazy loading on Android 8.0 and later
    val walk = if (includeDirectories) {
        toPath().walk(PathWalkOption.INCLUDE_DIRECTORIES)
    } else {
        toPath().walk()
    }
    return walk.map { it.toFile() }
}

fun File.children() = FileSequence(this)

suspend fun File.deleteAwait() = withContext(Dispatchers.IO) {
    delete() || deleteRecursively()
}

val File.creationTime
    get() = toPath().readAttributes<BasicFileAttributes>().creationTime().toMillis()

fun ZipFile.readText(entry: ZipEntry) = getInputStream(entry).bufferedReader().use {
    it.readText()
}

fun Sequence<File>.filterWith(filter: FileFilter): Sequence<File> = filter { f -> filter.accept(f) }

fun File.takeIfReadable() = takeIf { it.exists() && it.canRead() }
fun File.takeIfWriteable() = takeIf { it.exists() && it.canWrite() }

fun File.isNotEmpty() = length() != 0L