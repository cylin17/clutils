package com.cylin.clutils.view

import android.os.SystemClock
import android.view.View

/**
 * 避免重複點擊造成同時開啟多個頁面
 * 設定延遲時間或者使用預設的 1000 毫秒
 * 在延遲時間內的點擊一率忽略, 製造不重複點擊的效果
 */
abstract class OnGuardClickListener: View.OnClickListener {

    companion object {
        const val MINIMUM_CLICK_INTERVAL: Long = 1000
    }

    constructor() : super() {
        mInterval = MINIMUM_CLICK_INTERVAL
    }

    constructor(newInterval: Long) : super() {
        mInterval = newInterval
    }

    private val mInterval: Long

    private var mLastClickTime: Long = 0

    abstract fun onGuardClick(view: View?)

    override fun onClick(view: View?) {

        // 紀錄點擊的時間戳
        val currentTime = SystemClock.elapsedRealtime()

        // 計算與上次點擊間隔多長時間
        val elapsedTime = currentTime - mLastClickTime

        // 將當前時間更新至上次點擊時間(mLastClickTime)
        mLastClickTime = currentTime

        // 檢查間隔是否在設定的 目標間隔 內, 若小於 目標間隔 則丟棄
        if (elapsedTime <= mInterval) {
            return
        }

        onGuardClick(view)
    }
}
