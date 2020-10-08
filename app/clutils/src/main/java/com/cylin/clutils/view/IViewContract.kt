package com.cylin.clutils.view

import android.app.Activity
import android.content.Context

/**
 * @author Charlie
 * @Date on 2020-September-09
 * @Description
 */
interface IViewContract {

    val context: Context
        get() = act().applicationContext

    fun act(): Activity
    fun init()
    fun initView() {}
    fun initEvent() {}

    fun showError(err: String?)
}