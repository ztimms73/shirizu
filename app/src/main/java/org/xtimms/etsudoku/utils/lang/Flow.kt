package org.xtimms.etsudoku.utils.lang

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import java.util.concurrent.atomic.AtomicInteger

fun <T> Flow<T>.onEachWhile(action: suspend (T) -> Boolean): Flow<T> {
    var isCalled = false
    return onEach {
        if (!isCalled) {
            isCalled = action(it)
        }
    }.onCompletion {
        isCalled = false
    }
}

fun <T> Flow<T>.onEachIndexed(action: suspend (index: Int, T) -> Unit): Flow<T> {
    val counter = AtomicInteger(0)
    return transform { value ->
        action(counter.getAndIncrement(), value)
        return@transform emit(value)
    }
}

inline fun <T, R> Flow<List<T>>.mapItems(crossinline transform: (T) -> R): Flow<List<R>> {
    return map { list -> list.map(transform) }
}

fun <T> Flow<Collection<T>>.flatten(): Flow<T> = flow {
    collect { value ->
        for (item in value) {
            emit(item)
        }
    }
}