package org.xtimms.tokusho.utils.system

import okhttp3.Cookie

fun Cookie.newBuilder(): Cookie.Builder = Cookie.Builder().also { c ->
    c.name(name)
    c.value(value)
    if (persistent) {
        c.expiresAt(expiresAt)
    }
    if (hostOnly) {
        c.hostOnlyDomain(domain)
    } else {
        c.domain(domain)
    }
    c.path(path)
    if (secure) {
        c.secure()
    }
    if (httpOnly) {
        c.httpOnly()
    }
}