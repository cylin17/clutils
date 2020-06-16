package com.cylin.clutils.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView


class EditableImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {

    companion object {

        private val TAG = EditableImageView::class.java.simpleName
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.color = Color.RED
        paint.strokeWidth = 3f
        canvas?.drawRect(50f, 50f, 800f, 800f, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        android.util.Log.d(TAG, ">> EIV.onTouchEvent: ${event?.x}, ${event?.y}")
        return super.onTouchEvent(event)
    }
}