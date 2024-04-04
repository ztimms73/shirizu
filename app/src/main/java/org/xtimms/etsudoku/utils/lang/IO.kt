package org.xtimms.etsudoku.utils.lang

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.Source
import org.xtimms.etsudoku.utils.CancellableSource
import org.xtimms.etsudoku.utils.ProgressResponseBody

fun ResponseBody.withProgress(progressState: MutableStateFlow<Float>): ResponseBody {
    return ProgressResponseBody(this, progressState)
}

suspend fun Source.cancellable(): Source {
    val job = currentCoroutineContext()[Job]
    return CancellableSource(job, this)
}

suspend fun BufferedSink.writeAllCancellable(source: Source) = withContext(Dispatchers.IO) {
    writeAll(source.cancellable())
}