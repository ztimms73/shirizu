package org.xtimms.shirizu.utils.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun ConnectivityManager.isOnline(): Boolean {
    return activeNetwork?.let { isOnline(it) } ?: false
}

private fun ConnectivityManager.isOnline(network: Network): Boolean {
    val capabilities = getNetworkCapabilities(network) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}