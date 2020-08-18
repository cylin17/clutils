package com.cylin.clutilscompat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cylin.clutils.view.WaitingView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        WaitingView(this, icon = R.drawable.rotate_ring)
//            .setBackground(R.color.colorPrimary)
//            .setBackgroundColor(Color.LTGRAY)
//            .setWaitingText(R.string.app_name)
            .show()
    }
}
