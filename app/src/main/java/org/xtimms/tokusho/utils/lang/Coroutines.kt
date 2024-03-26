package org.xtimms.tokusho.utils.lang

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.lifecycle.RetainedLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.tokusho.utils.RetainedLifecycleCoroutineScope

suspend fun <T> withNonCancellableContext(block: suspend CoroutineScope.() -> T) =
    withContext(NonCancellable, block)

val processLifecycleScope: LifecycleCoroutineScope
    inline get() = ProcessLifecycleOwner.get().lifecycleScope

val RetainedLifecycle.lifecycleScope: RetainedLifecycleCoroutineScope
    inline get() = RetainedLifecycleCoroutineScope(this)

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Deferred<T>.peek(): T? = if (isCompleted) {
    runCatchingCancellable {
        getCompleted()
    }.getOrNull()
} else {
    null
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Deferred<T>.getCompletionResultOrNull(): Result<T>? = if (isCompleted) {
    getCompletionExceptionOrNull()?.let { error ->
        Result.failure(error)
    } ?: Result.success(getCompleted())
} else {
    null
}