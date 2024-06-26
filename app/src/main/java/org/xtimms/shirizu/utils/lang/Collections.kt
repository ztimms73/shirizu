package org.xtimms.shirizu.utils.lang

import androidx.collection.ArrayMap
import java.util.EnumSet

fun <T> Collection<T>.asArrayList(): ArrayList<T> = if (this is ArrayList<*>) {
    this as ArrayList<T>
} else {
    ArrayList(this)
}

fun <T> Sequence<T>.toListSorted(comparator: Comparator<T>): List<T> {
    return toMutableList().apply { sortWith(comparator) }
}

fun <T> List<T>.takeMostFrequent(limit: Int): List<T> {
    val map = ArrayMap<T, Int>(size)
    for (item in this) {
        map[item] = map.getOrDefault(item, 0) + 1
    }
    val entries = map.entries.sortedByDescending { it.value }
    val count = minOf(limit, entries.size)
    return buildList(count) {
        repeat(count) { i ->
            add(entries[i].key)
        }
    }
}

fun <T : R, R : Any> List<T>.insertSeparators(
    generator: (T?, T?) -> R?,
): List<R> {
    if (isEmpty()) return emptyList()
    val newList = mutableListOf<R>()
    for (i in -1..lastIndex) {
        val before = getOrNull(i)
        before?.let(newList::add)
        val after = getOrNull(i + 1)
        val separator = generator.invoke(before, after)
        separator?.let(newList::add)
    }
    return newList
}

fun <K, V> Map<K, V>.findKeyByValue(value: V): K? {
    for ((k, v) in entries) {
        if (v == value) {
            return k
        }
    }
    return null
}

fun Collection<*>?.sizeOrZero() = if (this == null) 0 else size

inline fun <reified E : Enum<E>> Collection<E>.toEnumSet(): EnumSet<E> = if (isEmpty()) {
    EnumSet.noneOf(E::class.java)
} else {
    EnumSet.copyOf(this)
}

fun <E> HashSet<E>.addOrRemove(value: E, shouldAdd: Boolean) {
    if (shouldAdd) {
        add(value)
    } else {
        remove(value)
    }
}