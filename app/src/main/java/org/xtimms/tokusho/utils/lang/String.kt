package org.xtimms.tokusho.utils.lang

inline fun <C : CharSequence?> C?.ifNullOrEmpty(defaultValue: () -> C): C {
    return if (this.isNullOrEmpty()) defaultValue() else this
}