package com.cylin.clutils.view.editor

import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * 旋轉變換
 * p.s. 旋轉默認 = 0
 */
class RotateTransformation(private val rotateRotationAngle: Float) : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val matrix = Matrix()
        //旋轉
        matrix.postRotate(rotateRotationAngle)
        //生成新的Bitmap
        return Bitmap.createBitmap(
            toTransform,
            0,
            0,
            toTransform.width,
            toTransform.height,
            matrix,
            true
        )
    }

    val id: String
        get() = rotateRotationAngle.toString() + ""

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

}