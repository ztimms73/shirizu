package org.xtimms.tokusho.sections.shelf.ext

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.model.ShelfCategory

val ShelfCategory.visualName: String
    @Composable
    get() = when {
        isSystemCategory -> stringResource(R.string.label_default)
        else -> name
    }

fun ShelfCategory.visualName(context: Context): String =
    when {
        isSystemCategory -> context.getString(R.string.label_default)
        else -> name
    }