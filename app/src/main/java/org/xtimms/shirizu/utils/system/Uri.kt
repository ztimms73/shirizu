package org.xtimms.shirizu.utils.system

import android.net.Uri
import androidx.core.net.toFile
import java.io.File
import java.util.zip.ZipFile

const val URI_SCHEME_FILE = "file"
const val URI_SCHEME_ZIP = "file+zip"

fun Uri.exists(): Boolean = when (scheme) {
    URI_SCHEME_FILE -> toFile().exists()
    URI_SCHEME_ZIP -> {
        val file = File(requireNotNull(schemeSpecificPart))
        file.exists() && ZipFile(file).use { it.getEntry(fragment) != null }
    }

    else -> unsupportedUri(this)
}

fun Uri.isTargetNotEmpty(): Boolean = when (scheme) {
    URI_SCHEME_FILE -> toFile().isNotEmpty()
    URI_SCHEME_ZIP -> {
        val file = File(requireNotNull(schemeSpecificPart))
        file.exists() && ZipFile(file).use { (it.getEntry(fragment)?.size ?: 0L) != 0L }
    }

    else -> unsupportedUri(this)
}

private fun unsupportedUri(uri: Uri): Nothing {
    throw IllegalArgumentException("Bad uri $uri: only schemes $URI_SCHEME_FILE and $URI_SCHEME_ZIP are supported")
}