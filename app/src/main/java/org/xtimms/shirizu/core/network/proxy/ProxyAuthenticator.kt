package org.xtimms.shirizu.core.network.proxy

import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.xtimms.shirizu.core.network.CommonHeaders
import org.xtimms.shirizu.core.prefs.AppSettings
import java.net.PasswordAuthentication
import java.net.Proxy

class ProxyAuthenticator: Authenticator, java.net.Authenticator() {

    init {
        setDefault(this)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (!isProxyEnabled()) {
            return null
        }
        if (response.request.header(CommonHeaders.PROXY_AUTHORIZATION) != null) {
            return null
        }
        val login = AppSettings.getProxyUser()
        val password = AppSettings.getProxyPassword()
        val credential = Credentials.basic(login, password)
        return response.request.newBuilder()
            .header(CommonHeaders.PROXY_AUTHORIZATION, credential)
            .build()
    }

    override fun getPasswordAuthentication(): PasswordAuthentication? {
        if (!isProxyEnabled()) {
            return null
        }
        val login = AppSettings.getProxyUser()
        val password = AppSettings.getProxyPassword()
        return PasswordAuthentication(login, password.toCharArray())
    }

    private fun isProxyEnabled() = AppSettings.getProxyType() != Proxy.Type.DIRECT.ordinal
}