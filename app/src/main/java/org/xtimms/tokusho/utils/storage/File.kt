package org.xtimms.tokusho.utils.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.xtimms.tokusho.BuildConfig
import java.io.File

/**
 * Returns the uri of a file
 *
 * @param context context of application
 */
fun File.getUriCompat(context: Context): Uri {
    return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", this)
}