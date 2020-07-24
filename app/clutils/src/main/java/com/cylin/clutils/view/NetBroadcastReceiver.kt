package com.cylin.clutils.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.cylin.clutils.net.NetworkUtil.isNetworkConnected

/**
 * @author Charlie
 * @Date on 2020-July-24
 * @Description
 */
class NetBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // 如果相等的話就說明網路狀態發生了變化
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            val netWorkState = isNetworkConnected(context)
            // 接口回調傳過去狀態的類型
            if (BaseActivity.netEvent != null) BaseActivity.netEvent!!.onNetChange(netWorkState)
        }
    }

    /**
     * 網路狀態變化介面
     */
    interface NetChangeListener {

        fun onNetChange(netWorkState: Boolean)
    }
}