package org.xtimms.tokusho.utils.lang

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat

fun <I> ActivityResultLauncher<I>.tryLaunch(
    input: I,
    options: ActivityOptionsCompat? = null,
): Boolean = runCatching {
    launch(input, options)
}.onFailure { e ->
    e.printStackTrace()
}.isSuccess

fun String.toUriOrNull() = if (isEmpty()) null else Uri.parse(this)