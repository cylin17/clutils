package com.cylin.clutils.view

import android.os.SystemClock
import android.view.View
import android.widget.AdapterView

/**
 * 設定延遲時間或者使用預設的 500 毫秒
 * 在延遲時間內的點擊一率忽略, 製造不重複點擊的效果
 */
abstract class OnItemDblClickListener : AdapterView.OnItemClickListener{

    companion object {
        const val MINIMUM_CLICK_INTERVAL: Long = 500
    }

    constructor() : super() {
        mInterval = MINIMUM_CLICK_INTERVAL
    }

    constructor(newInterval: Long) : super() {
        mInterval = newInterval
    }

    private val mInterval: Long
    private var mLastClickTime: Long = 0

    abstract fun onDblClickItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long)

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        val currentTime = SystemClock.elapsedRealtime()
        val elapsedTime = currentTime - mLastClickTime
        mLastClickTime = currentTime

        if (elapsedTime <= mInterval) {
            return
        }

        onDblClickItemClick(p0, p1, p2, p3)
    }
}