package com.cylin.clutils.view

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.cylin.clutils.R
import com.cylin.clutils.view.utils.setCustomDrawable
import kotlinx.android.synthetic.main.dialog_waiting.*
import kotlinx.android.synthetic.main.dialog_waiting.view.*

/**
 * @author Charlie
 * @Date on 2020-August-17
 * @Description
 */
class WaitingView(activity: Activity, message: String = "", icon: Int? = null) : Dialog(activity, R.style.LoadingDialog) {

    init {
        val view = View.inflate(context, R.layout.dialog_waiting, null)
        view.setCustomDrawable(defRadiusValue = 5f)

        view.progress_bar.apply {
            if (icon != null) {
                indeterminateDrawable = context.getDrawable(icon)
            }
        }

        view.message.apply {
            text = message
            visibility = if (message.isEmpty()) View.GONE else View.VISIBLE
        }
        setContentView(view)
    }

    override fun show() {
        if (isShowing) {
            return
        } else {
            super.show()
        }
    }
}

fun WaitingView.setWaitingText(resId: Int): WaitingView {
    return setWaitingText(context.getString(resId))
}

fun WaitingView.setWaitingText(text: String): WaitingView {
    message.apply {
        this.text = text
        visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
    }
    return this
}

fun WaitingView.setWaitingIcon(resId: Int): WaitingView {
    progress_bar.indeterminateDrawable = context.getDrawable(resId)
    return this
}

fun WaitingView.setBackground(resId: Int, corner: Float = 5f): WaitingView {
    waiting_view.setCustomDrawable(defRadiusValue = corner, solidColor = context.getColor(resId))
    return this
}

fun WaitingView.setBackgroundColor(color: Int, corner: Float = 5f): WaitingView {
    waiting_view.setCustomDrawable(defRadiusValue = corner, solidColor = color)
    return this
}
