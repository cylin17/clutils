package com.cylin.clutils.view.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DimenRes


fun EditText.text2String(): String {
    return text.toString()
}

fun EditText.isTextEmpty(): Boolean {
    return text.trim().isEmpty()
}

//returns dip(dp) dimension value in pixels
fun Context.dp2Px(value: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value,
    resources.displayMetrics
)

//return sp dimension value in pixels
fun Context.sp2Px(value: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    value,
    resources.displayMetrics
)

//converts px value into dip or sp
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

//the same for the views
fun View.dip(value: Int): Float = context.dp2Px(value.toFloat())
fun View.dip(value: Float): Float = context.dp2Px(value)
fun View.sp(value: Int): Float = context.sp2Px(value.toFloat())
fun View.sp(value: Float): Float = context.sp2Px(value)
fun View.px2dip(px: Int): Float = context.px2dip(px)
fun View.px2sp(px: Int): Float = context.px2sp(px)
fun View.dimen(@DimenRes resource: Int): Int = context.dimen(resource)

/**
 * 隐藏 IME 视窗
 */
fun Context.hideIme(view: View) {
    val imm: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * 繪製客製化背景
 *
 * @param radius 個別設定圓角的 value array, 1.2頂部左上角 3.4 頂部右上角 5.6 底部左下角 7.8底部右下角
 * @param defRadiusValue 圓角共用的 value, default 8f, 將 value 套用至四邊形所有圓角, radius and defRadiusValue 擇一使用即可
 * @param strokeWidth 邊框粗度 default 0f
 * @param strokeColor 邊框顏色, 不使用可以不用填寫, 預設為 null
 * @param solidColor 填滿顏色, 不使用可以不用填寫, 預設為 null
 */
fun View.setCustomDrawable(
    radius: FloatArray? = null,
    strokeWidth: Float = 0f,
    defRadiusValue: Float = 8f,
    strokeColor: Int? = null,
    solidColor: Int? = null,
    corners: Array<out Corner>? = null,
    dashWidth: Float = 0f,
    dashGap: Float = 0f,
): View {
    // 計算圓角大小
    val defRadV = dip(defRadiusValue).toFloat()

    // 存放 radius 的 array
    var radiusArray: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    // radius
    radius?.apply {
        forEachIndexed { index, fl ->
            this[index] = dip(fl).toFloat()
        }
        radiusArray = this
    }

    if (radius == null) {
        corners?.forEach {
            when (it) {
                Corner.TopLeft -> {
                    radiusArray[0] = defRadV
                    radiusArray[1] = defRadV
                }
                Corner.TopRight -> {
                    radiusArray[2] = defRadV
                    radiusArray[3] = defRadV
                }
                Corner.BottomLeft -> {
                    radiusArray[4] = defRadV
                    radiusArray[5] = defRadV
                }
                Corner.BottomRight -> {
                    radiusArray[6] = defRadV
                    radiusArray[7] = defRadV
                }
            }
        }
    }

    if (radius == null && corners == null) {
        //1.2顶部左上角 3.4 顶部右上角 5.6 底部左下角 7.8底部右下角
        radiusArray = floatArrayOf(defRadV, defRadV, defRadV, defRadV, defRadV, defRadV, defRadV, defRadV)
    }

    setBackgroundDrawable(GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE

        strokeColor?.run {
            //设置线宽，线颜色 stroke
            var gap = if (dashGap == 0f) {
                dashWidth
            } else {
                dashGap
            }
            setStroke(dip(strokeWidth).toInt(), strokeColor, dip(dashWidth), dip(gap))
        }

        solidColor?.run {
            //设置圈内颜色 solidColor
            setColor(solidColor)
        }

        //设置圆角
        cornerRadii = radiusArray
    })
    return this
}

/**
 * 四個角落
 */
enum class Corner {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight
}

fun setCorners(vararg corners: Corner): Array<out Corner> {
    return corners
}
