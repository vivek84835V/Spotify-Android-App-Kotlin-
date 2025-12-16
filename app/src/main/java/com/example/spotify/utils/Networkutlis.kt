package com.example.spotify.utils

import android.content.Context
import android.net.*
import android.os.Handler
import android.os.Looper
import androidx.core.content.getSystemService
import io.ktor.client.request.request

class Networkutlis(context: Context, private val OnConnect:()-> Unit, private val OnDisconnet:()-> Unit) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallBack = object: ConnectivityManager.NetworkCallback(){

        override fun onAvailable(network: Network) {
            if (isOnline(context)) {
                OnConnect()
            }
        }

        override fun onLost(network: Network) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isOnline(context)) {
                    OnDisconnet()
                }
            }, 1000)
        }
    }

    fun register(){
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request,networkCallBack)
    }

    fun unregister(){
        cm.unregisterNetworkCallback(networkCallBack)
    }

    companion object {
        fun isOnline(context: Context): Boolean {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        }
    }
}
