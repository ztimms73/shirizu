package org.xtimms.tokusho.utils.lang

import androidx.annotation.AnyThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.xtimms.tokusho.utils.Event

@Suppress("FunctionName")
fun <T> MutableEventFlow() = MutableStateFlow<Event<T>?>(null)

typealias EventFlow<T> = StateFlow<Event<T>?>

typealias MutableEventFlow<T> = MutableStateFlow<Event<T>?>

@AnyThread
fun <T> MutableEventFlow<T>.call(data: T) {
    value = Event(data)
}