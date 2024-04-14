package org.xtimms.shirizu.core.network.proxy

import org.xtimms.shirizu.core.prefs.AppSettings
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI

class AppProxySelector : ProxySelector() {

    init {
        setDefault(this)
    }

    private var cachedProxy: Proxy? = null

    override fun select(uri: URI?): List<Proxy> {
        return listOf(getProxy())
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
        ioe?.printStackTrace()
    }

    private fun getProxy(): Proxy {
        val type = AppSettings.getProxyType()
        val address = AppSettings.getProxyAddress()
        val port = AppSettings.getProxyPort()
        if (type == Proxy.Type.DIRECT.ordinal || address.isEmpty() || port == 0) {
            return Proxy.NO_PROXY
        }
        cachedProxy?.let {
            val addr = it.address() as? InetSocketAddress
            if (addr != null && it.type().ordinal == type && addr.port == port && addr.hostString == address) {
                return it
            }
        }
        val proxy = Proxy(Proxy.Type.entries[type], InetSocketAddress(address, port))
        cachedProxy = proxy
        return proxy
    }
}