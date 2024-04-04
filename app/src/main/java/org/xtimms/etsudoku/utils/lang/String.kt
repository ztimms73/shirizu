package org.xtimms.etsudoku.utils.lang

import android.net.Uri
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <C : CharSequence?> C?.ifNullOrEmpty(defaultValue: () -> C): C {
    return if (this.isNullOrEmpty()) defaultValue() else this
}

fun String.removeFirstAndLast() = substring(1, length - 1)

fun Array<String>.toNavArgument(): String = Uri.encode(Json.encodeToString(this))

fun String.longHashCode(): Long {
    var h = 1125899906842597L
    val len: Int = this.length
    for (i in 0 until len) {
        h = 31 * h + this[i].code
    }
    return h
}

fun CharSequence.sanitize(): CharSequence {
    return filterNot { c -> c.isReplacement() }
}

fun Char.isReplacement() = this in '\uFFF0'..'\uFFFF'

fun Float?.toStringPositiveValueOrUnknown() =
    if (this == 0f) "─" else this.toStringOrUnknown()

fun Float?.toStringOrUnknown() = this?.toString() ?: "─"