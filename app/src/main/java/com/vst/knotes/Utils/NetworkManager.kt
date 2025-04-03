package com.vst.knotes.Utils

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object  NetworkManager {
    private lateinit var networkStateReceiver: NetworkStateReceiver
    private var isRegistered = false
    fun init(context: Context) {
        networkStateReceiver = NetworkStateReceiver()
        registerReceiver(context)
    }

    fun registerReceiver(context: Context) {
        if (!isRegistered) {
            context.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            isRegistered = true
        }
    }

    fun unregisterReceiver(context: Context) {
        if (isRegistered) {
            context.unregisterReceiver(networkStateReceiver)
            isRegistered = false
        }
    }
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities != null && networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
    }
}