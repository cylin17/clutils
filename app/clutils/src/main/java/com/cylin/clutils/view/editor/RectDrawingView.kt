package com.cylin.clutils.view.editor

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import com.google.gson.Gson
import java.util.*

class RectDrawingView: AppCompatImageView {

    constructor(@NonNull context: Context): super(context) {
        setupRectDrawing()
    }

    constructor(@NonNull context: Context, attrs: AttributeSet?): super(context, attrs) {
        setupRectDrawing()
    }

    constructor(@NonNull context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        setupRectDrawing()
    }

    private var mRectViewChangeListener: RectViewChangeListener? = null

    companion object {

        private val TAG = RectDrawingView::class.java.simpleName
    }

    private var mBrushSize = 14f
    private var mBrushEraserSize = 50f
    private var mOpacity = 255
    private var mDrawPaint = Paint()

    private var mRect = Rect(0, 0, 0, 0)

    private var mDrawCanvas: Canvas? = null

    private var mDrawnPaths = Stack<RectPath>()
    private var mRedoPaths = Stack<RectPath>()

    private var mTouchX = 0f
    private var mTouchY = 0f

    var editable = true

    private inner class RectPath internal constructor(drawPath: Rect, drawPaint: Paint) {

        val drawPaint: Paint = Paint(drawPaint)
        val drawPath: Rect = Rect(drawPath)

        fun move(x: Int, y: Int) {
            drawPath.left += x
            drawPath.right += x
            drawPath.top += y
            drawPath.bottom += y
        }

        fun clone(): RectPath {
            val stringProject = Gson().toJson(this, RectPath::class.java)
            return Gson().fromJson<RectPath>(stringProject, RectPath::class.java)
        }
    }

    private fun setupRectDrawing() {
        setLayerType(LAYER_TYPE_HARDWARE, null)

        mDrawPaint = Paint()
        mDrawPaint.isAntiAlias = true
        mDrawPaint.isDither = true
        mDrawPaint.color = Color.RED
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mDrawPaint.strokeWidth = mBrushSize
        mDrawPaint.alpha = mOpacity
        mDrawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    fun setOpacity(@IntRange(from = 0, to = 255) opacity: Int) {
        this.mOpacity = opacity
    }

    fun setBrushSize(size: Float) {
        mBrushSize = size
    }

    fun setBrushColor(@ColorInt color: Int) {
        mDrawPaint.color = color
    }

    fun setBrushEraserSize(brushEraserSize: Float) {
        this.mBrushEraserSize = brushEraserSize
    }

    fun clearAll() {
        mDrawnPaths.clear()
        mDrawCanvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun setChangedListener(listener: RectViewChangeListener) {
        mRectViewChangeListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (h > 1) {
            val canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            mDrawCanvas = Canvas(canvasBitmap)
        }
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        requestLayout()

        if (editable) {
            for (rectPath in mDrawnPaths) {
                canvas?.drawRect(rectPath.drawPath, rectPath.drawPaint)
            }

            if (isMoved) {
                canvas?.drawRect(mRect, mDrawPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val touchX = event?.x
        val touchY = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> touchStart(touchX!!, touchY!!)
            MotionEvent.ACTION_MOVE -> touchMove(touchX!!, touchY!!)
            MotionEvent.ACTION_UP -> touchUp()
        }
        invalidate()
        return true
    }

    fun undo(): Boolean {

        if (!mDrawnPaths.empty()) {
            mRedoPaths.push(mDrawnPaths.pop())
            invalidate()
        }

        mRectViewChangeListener?.onViewRemoved(this)
        return !mDrawnPaths.empty()
    }

    fun redo(): Boolean {

        if (!mRedoPaths.empty()) {
            mDrawnPaths.push(mRedoPaths.pop())
            invalidate()
        }

        mRectViewChangeListener?.onViewAdd(this)
        return !mRedoPaths.empty()
    }

    private var isMoved = false

    private var isMoveMode = false

    fun switchMoveMode(enable: Boolean) {
        isMoveMode = enable
    }

    fun getMoveMode(): Boolean {
        return isMoveMode
    }

    private fun touchStart(x: Float, y: Float) {

        isMoved = false
        mTouchX = x
        mTouchY = y
        if (!isMoveMode) {
            mRedoPaths.clear()

            mRect = Rect(x.toInt(), y.toInt(), 0, 0)
        }
        mRectViewChangeListener?.onStartDrawing()
    }

    private fun touchMove(x: Float, y: Float) {

        isMoved = true
        if (isMoveMode) {
            if (!mDrawnPaths.empty()) {
                val diffX = x - mTouchX
                val diffY = y - mTouchY
                if (!mDrawnPaths.empty()) {
                    val lastPath = mDrawnPaths.pop()
                    lastPath.move(diffX.toInt(), diffY.toInt())
                    mDrawnPaths.push(RectPath(lastPath.drawPath, mDrawPaint))
                }
                mTouchX = x
                mTouchY = y
            }
        } else {
            val nowX = if (x > width) width.toFloat() else if (x < 0) 0f else x
            val nowY = if (y > height) height.toFloat() else if (y < 0) 0f else y
            mRect = Rect(mTouchX.toInt(), mTouchY.toInt(), nowX.toInt(), nowY.toInt())
        }
    }

    private fun touchUp() {

        if (isMoved) {
            if (isMoveMode) {
                mRectViewChangeListener?.onStopDrawing()
            } else {
                mDrawCanvas?.drawRect(mRect, mDrawPaint)

                mDrawnPaths.push(RectPath(mRect, mDrawPaint))

                mRect = Rect(0, 0, 0, 0)

                mRectViewChangeListener?.onStopDrawing()
                mRectViewChangeListener?.onViewAdd(this)
            }
        }
    }

}