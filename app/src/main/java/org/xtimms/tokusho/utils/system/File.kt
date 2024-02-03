package org.xtimms.tokusho.utils.system

import android.content.Context

fun Context.getFileProvider() = "$packageName.provider"