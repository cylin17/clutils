package com.cylin.clutils.view

import android.app.Activity
import android.content.Context

/**
 * @author Charlie
 * @Date on 2020-September-09
 * @Description
 */
interface IViewContract {

    fun activity(): Activity
    fun context(): Context
    fun initView()
    fun initEvent()

    fun showError(err: String)
}