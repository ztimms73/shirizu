package org.xtimms.tokusho.utils.system

import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.internal.closeQuietly
import org.jsoup.HttpStatusException
import java.net.HttpURLConnection

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

val HttpUrl.isHttpOrHttps: Boolean
    get() {
        val s = scheme.lowercase()
        return s == "https" || s == "http"
    }

fun Response.ensureSuccess() = apply {
    if (!isSuccessful || code == HttpURLConnection.HTTP_NO_CONTENT) {
        closeQuietly()
        throw HttpStatusException(message, code, request.url.toString())
    }
}