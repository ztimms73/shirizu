package org.xtimms.shirizu.core.parser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.WebView
import androidx.annotation.MainThread
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.bitmap.Bitmap
import org.koitharu.kotatsu.parsers.config.MangaSourceConfig
import org.koitharu.kotatsu.parsers.model.MangaSource
import org.koitharu.kotatsu.parsers.network.UserAgents
import org.koitharu.kotatsu.parsers.util.SuspendLazy
import org.xtimms.shirizu.core.network.MangaHttpClient
import org.xtimms.shirizu.core.network.cookies.MutableCookieJar
import org.xtimms.shirizu.core.prefs.SourceSettings
import org.xtimms.shirizu.utils.system.configureForParser
import org.xtimms.shirizu.utils.system.requireBody
import org.xtimms.shirizu.utils.system.sanitizeHeaderValue
import org.xtimms.shirizu.utils.system.toList
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class MangaLoaderContextImpl @Inject constructor(
    @MangaHttpClient override val httpClient: OkHttpClient,
    override val cookieJar: MutableCookieJar,
    @ApplicationContext private val androidContext: Context,
) : MangaLoaderContext() {

    private var webViewCached: WeakReference<WebView>? = null

    private val webViewUserAgent by lazy { obtainWebViewUserAgent() }

    @SuppressLint("SetJavaScriptEnabled")
    override suspend fun evaluateJs(script: String): String? = withContext(Dispatchers.Main.immediate) {
        val webView = webViewCached?.get() ?: WebView(androidContext).also {
            it.settings.javaScriptEnabled = true
            webViewCached = WeakReference(it)
        }
        suspendCoroutine { cont ->
            webView.evaluateJavascript(script) { result ->
                cont.resume(result?.takeUnless { it == "null" })
            }
        }
    }

    override fun getConfig(source: MangaSource): MangaSourceConfig {
        return SourceSettings(androidContext, source)
    }

    override fun getDefaultUserAgent(): String = webViewUserAgent

    override fun encodeBase64(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    override fun decodeBase64(data: String): ByteArray {
        return Base64.decode(data, Base64.DEFAULT)
    }

    override fun getPreferredLocales(): List<Locale> {
        return LocaleListCompat.getAdjustedDefault().toList()
    }

    override fun createBitmap(width: Int, height: Int): Bitmap {
        return BitmapWrapper.create(width, height)
    }

    override fun redrawImageResponse(
        response: Response,
        redraw: (image: Bitmap) -> Bitmap
    ): Response {
        val image = response.requireBody().byteStream()

        val opts = BitmapFactory.Options()
        opts.inMutable = true
        val bitmap = BitmapFactory.decodeStream(image, null, opts) ?: error("Cannot decode bitmap")
        val result = redraw(BitmapWrapper.create(bitmap)) as BitmapWrapper

        val body = Buffer().also {
            result.compressTo(it.outputStream())
        }.asResponseBody("image/jpeg".toMediaType())

        return response.newBuilder()
            .body(body)
            .build()
    }

    @MainThread
    private fun obtainWebView(): WebView {
        return webViewCached?.get() ?: WebView(androidContext).also {
            it.configureForParser(null)
            webViewCached = WeakReference(it)
        }
    }

    private fun obtainWebViewUserAgent(): String {
        val mainDispatcher = Dispatchers.Main.immediate
        return if (!mainDispatcher.isDispatchNeeded(EmptyCoroutineContext)) {
            obtainWebViewUserAgentImpl()
        } else {
            runBlocking(mainDispatcher) {
                obtainWebViewUserAgentImpl()
            }
        }
    }

    @MainThread
    private fun obtainWebViewUserAgentImpl() = runCatching {
        obtainWebView().settings.userAgentString.sanitizeHeaderValue()
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrDefault(UserAgents.FIREFOX_MOBILE)
}