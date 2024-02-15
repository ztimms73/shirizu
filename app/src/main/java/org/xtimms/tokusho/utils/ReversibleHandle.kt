package org.xtimms.tokusho.utils

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import org.xtimms.tokusho.utils.lang.processLifecycleScope

fun interface ReversibleHandle {

    suspend fun reverse()
}

@OptIn(ExperimentalCoroutinesApi::class)
fun ReversibleHandle.reverseAsync() = processLifecycleScope.launch(Dispatchers.Default, CoroutineStart.ATOMIC) {
    runCatchingCancellable {
        withContext(NonCancellable) {
            reverse()
        }
    }.onFailure {

    }
}

operator fun ReversibleHandle.plus(other: ReversibleHandle) = ReversibleHandle {
    this.reverse()
    other.reverse()
}