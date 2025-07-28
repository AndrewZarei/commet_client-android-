package com.blockchain.commet.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.net.NetworkInterface
import java.util.ArrayList
import java.util.Collections

/***
 * check internet is available or not
 */
@Suppress("DEPRECATION")
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }
    }
    return result
}

/**
 * check vpn is on
 * @return boolean
 */
fun isVpnOn(): Boolean {
    val networkList = mutableListOf<String>()
    try {
        for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (networkInterface.isUp) networkList.add(networkInterface.name)
        }
    } catch (ignored: Exception) {
    }
    return networkList.contains("tun0")
}

/***
 *  custom getParcelable instead of default getParcelable that is deprecated
 */
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

/***
 * extension function
 * get parcelable model from intent
 */
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

/***
 *  custom getParcelable instead of default getParcelable that is deprecated
 */
inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

/***
 * extension function
 * set data for backStack Fragments
 */
fun <T : Any> Fragment.setBackStackData(key: String, data: T, doBack : Boolean = true) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if(doBack)
        findNavController().popBackStack()
}

/***
 * extension function
 * set data for backStack Fragments
 */
fun <T : Any> Fragment.removeBackStackData(key: String) {
    findNavController().previousBackStackEntry?.savedStateHandle?.remove<T>(key)
}

/***
 * extension function
 * get data from backStack Fragments
 */
fun <T : Any> Fragment.getBackStackData(key: String, result: (T) -> (Unit)) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner) {
            result(it)
        }
}