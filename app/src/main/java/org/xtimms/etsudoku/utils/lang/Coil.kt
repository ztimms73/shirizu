package org.xtimms.etsudoku.utils.lang

import androidx.core.graphics.drawable.toBitmap
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult

fun ImageResult.toBitmapOrNull() = when (this) {
    is SuccessResult -> try {
        drawable.toBitmap()
    } catch (_: Throwable) {
        null
    }

    is ErrorResult -> null
}