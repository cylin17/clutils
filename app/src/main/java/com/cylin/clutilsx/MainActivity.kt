package com.cylin.clutilsx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cylin.clutils.clog
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

        clog(Test(), "data")
        clog(123.4324)
    }

    data class Test(
        var text: String = "123"
    )
}
