package com.cylin.clutils.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils

/**
 * @author Charlie
 * @Date on 2020-July-24
 * @Description
 */
object NetworkUtil {

    const val NETTYPE_WIFI = 0x01
    const val NETTYPE_CMWAP = 0x02
    const val NETTYPE_CMNET = 0x03

    /**
     * 檢測網路是否可用
     */
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var ni: NetworkInfo? = null
        if (cm != null) {
            ni = cm.activeNetworkInfo
        }
        return ni != null && ni.isConnectedOrConnecting
    }

    /**
     * 取得當前網路類型
     *
     * @return 0：沒有網絡 1：WIFI網絡 2：WAP網絡 3：NET網絡
     */
    fun getNetworkType(context: Context): Int {
        var netType = 0
        val connectivityManager = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            networkInfo = connectivityManager.activeNetworkInfo
        }
        if (networkInfo == null) {
            return netType
        }
        val nType = networkInfo.type
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            val extraInfo = networkInfo.extraInfo
            if (!TextUtils.isEmpty(extraInfo)) {
                netType = if (extraInfo.toLowerCase() == "cmnet") {
                    NETTYPE_CMNET
                } else {
                    NETTYPE_CMWAP
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI
        }
        return netType
    }
}