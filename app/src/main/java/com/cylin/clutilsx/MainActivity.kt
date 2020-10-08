package com.cylin.clutilsx

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cylin.clutils.clog
import com.cylin.clutils.view.IViewContract
import com.cylin.clutils.view.WaitingView
import com.cylin.clutils.view.utils.setCustomDrawable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IViewContract {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    override fun act(): Activity = this

    override fun init() {
        TODO("Not yet implemented")
    }

    override fun initView() {
//        WaitingView(this, icon = R.drawable.rotate_ring)
//            .setBackground(R.color.colorPrimary)
//            .setBackgroundColor(Color.LTGRAY)
//            .setWaitingText(R.string.app_name)
//            .show()

        clog(Test(), "data")
        clog(123.4324)
        Test().testFunc()
        Test2().testFunc()
        Test3.testFunc()
        testFunc()

        textView.setCustomDrawable(dashWidth = 5f, strokeColor = Color.BLACK, strokeWidth = 2f)

        clog(context)
        clog(act().applicationContext)
    }

    override fun showError(err: String?) {
        TODO("Not yet implemented")
    }

    class Test2 {
        fun testFunc() {
            clog("world")
        }
    }

    data class Test(
        var text: String = "123"
    ) {
        fun testFunc() {
            clog("hello")
        }
    }
}

object Test3 {
    fun testFunc() {
        clog("testing")
    }
}

fun MainActivity.testFunc() {
    clog("what?")
}