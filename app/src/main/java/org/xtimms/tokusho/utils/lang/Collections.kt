package org.xtimms.tokusho.utils.lang

fun <T> Collection<T>.asArrayList(): ArrayList<T> = if (this is ArrayList<*>) {
    this as ArrayList<T>
} else {
    ArrayList(this)
}

fun <T> Sequence<T>.toListSorted(comparator: Comparator<T>): List<T> {
    return toMutableList().apply { sortWith(comparator) }
}