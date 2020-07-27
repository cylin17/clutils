package com.cylin.clutils.view.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Camera
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import java.util.*

class OverCameraView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var touchFocusRect: Rect? = null//焦点附近设置矩形区域作为对焦区域
    private var touchFocusPaint: Paint? = null//新建画笔

    init {
        init(context)
    }

    private fun init(context: Context) {
        //画笔设置
        touchFocusPaint = Paint()
        touchFocusPaint!!.color = Color.GREEN
        touchFocusPaint!!.style = Paint.Style.STROKE
        touchFocusPaint!!.strokeWidth = 3f
    }

    //对焦并绘制对焦矩形框
    fun setTouchFoucusRect(camera: Camera, autoFocusCallback: Camera.AutoFocusCallback, x: Float, y: Float) {
        //以焦点为中心，宽度为200的矩形框
        touchFocusRect = Rect((x - 100).toInt(), (y - 100).toInt(), (x + 100).toInt(), (y + 100).toInt())
        android.util.Log.d("Test", ">> touchFocusRect: $touchFocusRect, $maxWidth")

        //对焦区域
        val targetFocusRect = Rect(
                touchFocusRect!!.left / 2,
                touchFocusRect!!.top / 2,
                touchFocusRect!!.right / 2,
                touchFocusRect!!.bottom / 2)

        doTouchFocus(camera, autoFocusCallback, targetFocusRect)//对焦
        postInvalidate()//刷新界面，调用onDraw(Canvas canvas)函数绘制矩形框
    }

    //设置camera参数，并完成对焦
    private fun doTouchFocus(camera: Camera, autoFocusCallback: Camera.AutoFocusCallback, tfocusRect: Rect) {
        try {
            val focusList = ArrayList<Camera.Area>()
            val focusArea = Camera.Area(tfocusRect, 1000)//相机参数：对焦区域
            focusList.add(focusArea)

            val para = camera.parameters
            para.focusAreas = focusList
            para.meteringAreas = focusList
            camera.parameters = para//相机参数生效
            camera.autoFocus(autoFocusCallback)
        } catch (e: Exception) {}

    }

    //对焦完成后，清除对焦矩形框
    fun disDrawTouchFocusRect() {
        touchFocusRect = null//将对焦区域设置为null，刷新界面后对焦框消失
        postInvalidate()//刷新界面，调用onDraw(Canvas canvas)函数
    }

    override fun onDraw(canvas: Canvas) { //在画布上绘图，postInvalidate()后自动调用
        drawTouchFocusRect(canvas)
        super.onDraw(canvas)
    }

    private fun drawTouchFocusRect(canvas: Canvas) {
        if (null != touchFocusRect) {
            //根据对焦区域targetFocusRect，绘制自己想要的对焦框样式，本文在矩形四个角取L形状
            //左下角
            canvas.drawRect((touchFocusRect!!.left - 2).toFloat(), touchFocusRect!!.bottom.toFloat(), (touchFocusRect!!.left + 20).toFloat(), (touchFocusRect!!.bottom + 2).toFloat(), touchFocusPaint!!)
            canvas.drawRect((touchFocusRect!!.left - 2).toFloat(), (touchFocusRect!!.bottom - 20).toFloat(), touchFocusRect!!.left.toFloat(), touchFocusRect!!.bottom.toFloat(), touchFocusPaint!!)
            //左上角
            canvas.drawRect((touchFocusRect!!.left - 2).toFloat(), (touchFocusRect!!.top - 2).toFloat(), (touchFocusRect!!.left + 20).toFloat(), touchFocusRect!!.top.toFloat(), touchFocusPaint!!)
            canvas.drawRect((touchFocusRect!!.left - 2).toFloat(), touchFocusRect!!.top.toFloat(), touchFocusRect!!.left.toFloat(), (touchFocusRect!!.top + 20).toFloat(), touchFocusPaint!!)
            //右上角
            canvas.drawRect((touchFocusRect!!.right - 20).toFloat(), (touchFocusRect!!.top - 2).toFloat(), (touchFocusRect!!.right + 2).toFloat(), touchFocusRect!!.top.toFloat(), touchFocusPaint!!)
            canvas.drawRect(touchFocusRect!!.right.toFloat(), touchFocusRect!!.top.toFloat(), (touchFocusRect!!.right + 2).toFloat(), (touchFocusRect!!.top + 20).toFloat(), touchFocusPaint!!)
            //右下角
            canvas.drawRect((touchFocusRect!!.right - 20).toFloat(), touchFocusRect!!.bottom.toFloat(), (touchFocusRect!!.right + 2).toFloat(), (touchFocusRect!!.bottom + 2).toFloat(), touchFocusPaint!!)
            canvas.drawRect(touchFocusRect!!.right.toFloat(), (touchFocusRect!!.bottom - 20).toFloat(), (touchFocusRect!!.right + 2).toFloat(), touchFocusRect!!.bottom.toFloat(), touchFocusPaint!!)
        }
    }

}
