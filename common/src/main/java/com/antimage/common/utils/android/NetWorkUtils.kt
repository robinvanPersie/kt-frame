package com.antimage.common.utils.android

import android.content.Context
import android.net.ConnectivityManager

object NetWorkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (connectivityManager == null) false
        else {
            var networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        }
    }
}