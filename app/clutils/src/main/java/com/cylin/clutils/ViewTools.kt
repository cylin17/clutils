package com.cylin.clutils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.annotation.DimenRes
import android.view.View
import android.widget.EditText

fun EditText.text2String(): String {
    return text.toString()
}

fun EditText.isTextEmpty(): Boolean {
    return text.trim().isEmpty()
}

val Any.TAG: String
    get() = this.javaClass.simpleName

//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

//the same for the views
inline fun View.dip(value: Int): Int = context.dip(value)
inline fun View.dip(value: Float): Int = context.dip(value)
inline fun View.sp(value: Int): Int = context.sp(value)
inline fun View.sp(value: Float): Int = context.sp(value)
inline fun View.px2dip(px: Int): Float = context.px2dip(px)
inline fun View.px2sp(px: Int): Float = context.px2sp(px)
inline fun View.dimen(@DimenRes resource: Int): Int = context.dimen(resource)

/**
 * 繪製按鈕背景
 *
 * @param radius 個別設定圓角的 value array, 1.2顶部左上角 3.4 顶部右上角 5.6 底部左下角 7.8底部右下角
 * @param defRadiusValue 圓角共用的 value, default 8f, 將 value 套用至四邊形所有圓角, radius and defRadiusValue 擇一使用即可
 * @param strokeWidth 邊框粗度 default 0f
 * @param strokeColor 邊框顏色, 不使用可以不用填寫, 預設為 null
 * @param solidColor 填滿顏色, 不使用可以不用填寫, 預設為 null
 */
fun View.setCustomDrawable(radius: FloatArray? = null,
                           strokeWidth: Float = 0f,
                           defRadiusValue: Float = 8f,
                           strokeColor: Int? = null,
                           solidColor: Int? = null): View {
    var radiusArray = radius
    //设置默认圆角大小
    val defRadV = dip(defRadiusValue).toFloat()
    if (radiusArray == null || radiusArray.isEmpty()) {
        //1.2顶部左上角 3.4 顶部右上角 5.6 底部左下角 7.8底部右下角
        radiusArray = floatArrayOf(defRadV, defRadV, defRadV, defRadV, defRadV, defRadV, defRadV, defRadV)
    }
    this.setBackgroundDrawable(GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE

        strokeColor?.run {
            //设置线宽，线颜色 stroke
            setStroke(dip(strokeWidth), strokeColor)
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
