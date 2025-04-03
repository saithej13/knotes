package com.vst.knotes.Utils

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager

class MyApplication : Application() {
    private lateinit var networkStateReceiver: NetworkStateReceiver
    companion object {
        private var instance: MyApplication? = null
//        var APP_DB_LOCK: String = "lock"
        var APP_DB_LOCK: Any = Any()
        fun getInstance(): MyApplication? {
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        networkStateReceiver = NetworkStateReceiver()
        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(networkStateReceiver)
    }
}