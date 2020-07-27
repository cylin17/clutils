package com.cylin.clutils.glide

import android.content.Context
import android.graphics.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.security.MessageDigest

/**
 * @author Charlie
 * @Date on 2020-July-22
 * @Description
 */
class RoundedCornersTransform(context: Context, radius: Float): Transformation<Bitmap> {

    private var mBitmapPool: BitmapPool = Glide.get(context).bitmapPool
    private var radius: Float = radius
    private var isLeftTop = false
    private var isRightTop = false
    private var isLeftBottom = false
    private var isRightBotoom = false

    /**
     * 需要設定圓角的部份
     *
     * @param leftTop     左上角
     * @param rightTop    右上角
     * @param leftBottom  左下角
     * @param rightBottom 右下角
     */
    fun setNeedCorner(leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean): RoundedCornersTransform {
        isLeftTop = leftTop
        isRightTop = rightTop
        isLeftBottom = leftBottom
        isRightBotoom = rightBottom
        return this
    }

    override fun transform(context: Context, resource: Resource<Bitmap>, outWidth: Int, outHeight: Int): Resource<Bitmap> {
        val source = resource.get()
        var finalWidth: Int
        var finalHeight: Int
        // 輸出目標的寬高比例
        var scale: Float
        if (outWidth > outHeight) {
            // 如果 輸出的寬度 > 輸出高度 求高寬比
            scale = outHeight.toFloat() / outWidth.toFloat()
            finalWidth = source.width
            // 固定元圖的寬度，求最終高度
            finalHeight = (source.width.toFloat() * scale).toInt()
            if (finalHeight > source.height) {
                // 如果 求出的最終高度 > 原圖高度 求寬高比
                scale = outWidth.toFloat() / outHeight.toFloat()
                finalHeight = source.height
                // 固定原圖高度，求最終寬度
                finalWidth = (source.height.toFloat() * scale).toInt()
            }
        } else if (outWidth < outHeight) {
            // 如果 輸出寬度 < 輸出高度 求寬高比
            scale = outWidth.toFloat() / outHeight.toFloat()
            finalHeight = source.height
            // 固定原圖高度,求最终寬度
            finalWidth = (source.height.toFloat() * scale).toInt()
            if (finalWidth > source.width) {
                // 如果 求出的最终寬度 > 原圖寬度 求高寬比
                scale = outHeight.toFloat() / outWidth.toFloat()
                finalWidth = source.width
                finalHeight = (source.width.toFloat() * scale).toInt()
            }
        } else {
            // 如果 輸出寬度=輸出高度
            finalHeight = source.height
            finalWidth = finalHeight
        }

        // 修正圓角
        radius *= finalHeight.toFloat() / outHeight.toFloat()
        var outBitmap = mBitmapPool[finalWidth, finalHeight, Bitmap.Config.ARGB_8888]
        if (outBitmap == null) {
            outBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(outBitmap)
        val paint = Paint()
        // 關聯畫筆繪製的原圖Bitmap
        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        // 計算中心位置，進行偏移
        val width = (source.width - finalWidth) / 2
        val height = (source.height - finalHeight) / 2
        if (width != 0 || height != 0) {
            val matrix = Matrix()
            matrix.setTranslate((-width).toFloat(), (-height).toFloat())
            shader.setLocalMatrix(matrix)
        }
        paint.shader = shader
        paint.isAntiAlias = true
        val rectF = RectF(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat())
        // 先繪畫圓角矩形
        canvas.drawRoundRect(rectF, radius, radius, paint)

        // 左上角圓角
        if (!isLeftTop) {
            canvas.drawRect(0f, 0f, radius, radius, paint)
        }
        // 右上角圓角
        if (!isRightTop) {
            canvas.drawRect(canvas.width - radius, 0f, canvas.width.toFloat(), radius, paint)
        }
        // 左下角圓角
        if (!isLeftBottom) {
            canvas.drawRect(0f, canvas.height - radius, radius, canvas.height.toFloat(), paint)
        }
        // 右下角圓角
        if (!isRightBotoom) {
            canvas.drawRect(canvas.width - radius, canvas.height - radius, canvas.width.toFloat(), canvas.height.toFloat(), paint)
        }

        return BitmapResource.obtain(outBitmap, mBitmapPool)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        // Nothings to do.
    }

}