package com.cylin.clutils.view.editor;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * 旋轉變換
 */

public class RotateTransformation extends BitmapTransformation {

    //旋轉默認0
    private float rotateRotationAngle;

    public RotateTransformation(float rotateRotationAngle) {
        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        //旋轉
        matrix.postRotate(rotateRotationAngle);
        //生成新的Bitmap
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    public String getId() {
        return rotateRotationAngle + "";
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        // TODO updateDiskCacheKey
    }
}
