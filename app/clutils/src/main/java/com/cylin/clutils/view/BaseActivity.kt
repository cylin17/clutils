package com.cylin.clutils.view

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cylin.clutils.view.utils.ActivityUtils


abstract class BaseActivity : AppCompatActivity(), NetBroadcastReceiver.NetChangeListener {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隱藏標題欄
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        // 沉浸效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明狀態欄
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 透明導航欄
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }

        // 添加到Activity工具類
        ActivityUtils.instance?.addActivity(this)

        // 初始化netEvent
        netEvent = this

        // 執行初始化方法
        init()
    }

    // 抽象 - 初始化方法，可以對數據進行初始化
    protected abstract fun init()

    override fun onResume() {
        super.onResume()
        val resources: Resources = this.resources
        val configuration: Configuration = resources.configuration
        configuration.fontScale = 1F
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onDestroy() {
        // Activity銷毀時，提示系統回收
        // System.gc();
        netEvent = null
        // 移除Activity
        ActivityUtils.instance?.removeActivity(this)
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 點擊手機上的返回鍵，返回上一層
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 移除Activity
            ActivityUtils.instance?.removeActivity(this)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 權限檢查方法，false代表沒有該權限，ture代表有該權限
     */
    fun hasPermission(vararg permissions: String?): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 權限請求方法
     */
    fun requestPermission(code: Int, vararg permissions: String?) {
        ActivityCompat.requestPermissions(this, permissions, code)
    }

    /**
     * 處理請求權限結果事件
     *
     * @param requestCode 請求碼
     * @param permissions 權限組
     * @param grantResults 結果集
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        doRequestPermissionsResult(requestCode, grantResults)
    }

    /**
     * 處理請求權限結果事件
     *
     * @param requestCode 請求碼
     * @param grantResults 結果集
     */
    fun doRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {}

    /**
     * 網絡狀態改變時間監聽
     *
     * @param netWorkState true有網絡，false無網絡
     */
    override fun onNetChange(netWorkState: Boolean) {}

    companion object {

        var netEvent // 網絡狀態改變監聽事件
                : NetBroadcastReceiver.NetChangeListener? = null
    }
}
