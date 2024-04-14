package org.xtimms.shirizu.core.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.xtimms.shirizu.core.exceptions.CloudflareProtectedException
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAVAILABLE

class CloudflareInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == HTTP_FORBIDDEN || response.code == HTTP_UNAVAILABLE) {
            val content = response.body?.let { response.peekBody(Long.MAX_VALUE) }?.byteStream()?.use {
                Jsoup.parse(it, Charsets.UTF_8.name(), response.request.url.toString())
            } ?: return response
            if (content.getElementById("challenge-error-title") != null) {
                val request = response.request
                response.closeQuietly()
                throw CloudflareProtectedException(
                    url = request.url.toString(),
                    source = request.tag(MangaSource::class.java),
                    headers = request.headers,
                )
            }
        }
        return response
    }
}