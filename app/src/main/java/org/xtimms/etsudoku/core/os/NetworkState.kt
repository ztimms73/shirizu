package org.xtimms.etsudoku.core.os

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.first
import org.xtimms.etsudoku.utils.MediatorStateFlow
import org.xtimms.etsudoku.utils.system.isOnline

class NetworkState(
    private val connectivityManager: ConnectivityManager,
) : MediatorStateFlow<Boolean>(connectivityManager.isOnline()) {

    private val callback = NetworkCallbackImpl()

    @Synchronized
    override fun onActive() {
        invalidate()
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    @Synchronized
    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(callback)
    }

    suspend fun awaitForConnection() {
        if (value) {
            return
        }
        first { it }
    }

    private fun invalidate() {
        publishValue(connectivityManager.isOnline())
    }

    private inner class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) = invalidate()

        override fun onLost(network: Network) = invalidate()

        override fun onUnavailable() = invalidate()
    }
}