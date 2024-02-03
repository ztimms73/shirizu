package org.xtimms.tokusho.utils.system

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
    val capabilities = getNetworkCapabilities(network)
    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}