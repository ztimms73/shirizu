package org.xtimms.tokusho.utils

import android.content.Context
import android.webkit.WebView

object WebViewUtil {

    fun getVersion(context: Context): String {
        val webView = WebView.getCurrentWebViewPackage() ?: return "o_O"
        val pm = context.packageManager
        val label = webView.applicationInfo.loadLabel(pm)
        val version = webView.versionName
        return "$label $version"
    }

}