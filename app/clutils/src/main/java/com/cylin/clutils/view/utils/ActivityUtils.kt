package com.cylin.clutils.view.utils

import android.app.Activity
import java.util.*

/**
 * @author Charlie
 * @Date on 2020-July-24
 * @Description
 */
class ActivityUtils {

    private var activityList: MutableList<Activity>? = ArrayList()

    /**
     * 添加Activity到容器中
     *
     * @param activity
     */
    fun addActivity(activity: Activity) {
        if (activityList == null) activityList = ArrayList()
        activityList!!.add(activity)
    }

    /**
     * 移除Activity
     *
     * @param activity
     */
    fun removeActivity(activity: Activity) {
        if (activityList != null) activityList!!.remove(activity)
    }

    /**
     * 遍歷所有Activity並finish
     */
    fun exitSystem() {
        for (activity in activityList!!) {
            activity?.finish()
        }
        // 退出進程
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    companion object {
        /**
         * 單例模式中獲取唯一的ExitApplication實例
         *
         * @return
         */
        @get:Synchronized
        var instance: ActivityUtils? = null
            get() {
                if (null == field) {
                    field = ActivityUtils()
                }
                return field
            }
            private set
    }
}