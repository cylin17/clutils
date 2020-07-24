package com.cylin.clutils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.util.Log
import android.view.Surface
import java.util.*

object CameraUtils {

    // 降序
    private val dropSizeComparator = CameraDropSizeComparator()

    // 升序
    private val ascendSizeComparator = CameraAscendSizeComparator()
    fun getRecorderRotation(cameraId: Int): Int {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        return info.orientation
    }

    /**
     * 获取所有支持的返回视频尺寸
     *
     * @param list
     * @param minHeight
     * @return
     */
    fun getPropVideoSize(
        list: List<Camera.Size>,
        minHeight: Int
    ): Camera.Size {
        Collections.sort(list, ascendSizeComparator)
        var i = 0
        for (s in list) {
            if (s.height >= minHeight) {
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0 //如果没找到，就选最小的size
        }
        return list[i]
    }

    /**
     * 保证预览方向正确
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    fun setCameraDisplayOrientation(
        activity: Activity,
        cameraId: Int, camera: Camera
    ) {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    fun setTakePicktrueOrientation(id: Int, bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        val info = CameraInfo()
        Camera.getCameraInfo(id, info)
        bitmap = rotatingImageView(id, info.orientation, bitmap)
        return bitmap
    }

    /**
     * 把相机拍照返回照片转正
     *
     * @param angle 旋转角度
     * @return bitmap 图片
     */
    fun rotatingImageView(id: Int, angle: Int, bitmap: Bitmap): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        //加入翻转 把相机拍照返回照片转正
        if (id == 1) {
            matrix.postScale(-1f, 1f)
        }
        // 创建新的图片
        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true
        )
    }

    /**
     * 获取所有支持的预览尺寸
     *
     * @param list
     * @param minWidth
     * @return
     */
    fun getPropPreviewSize(
        list: List<Camera.Size>,
        minWidth: Int
    ): Camera.Size {
        Collections.sort(list, ascendSizeComparator)
        var i = 0
        for (s in list) {
            if (s.width >= minWidth) {
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0 //如果没找到，就选最小的size
        }
        return list[i]
    }

    /**
     * 获取所有支持的返回图片尺寸
     *
     * @param list
     * @param minWidth
     * @return
     */
    fun getPropPictureSize(
        list: List<Camera.Size>,
        minWidth: Int
    ): Camera.Size {
        Collections.sort(list, ascendSizeComparator)
        var i = 0
        for (s in list) {
            if (s.width >= minWidth) {
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0 //如果没找到，就选最小的size
        }
        return list[i]
    }

    /**
     * 获取所有支持的返回视频尺寸
     *
     * @param list
     * @param minHeight
     * @return
     */
    fun getPropSizeForHeight(
        list: List<Camera.Size>,
        minHeight: Int
    ): Camera.Size {
        Collections.sort(
            list,
            CameraAscendSizeComparatorForHeight()
        )
        var i = 0
        for (s in list) {
            if (s.height >= minHeight) {
                Log.i(TAG, "s.height===" + s.height)
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0 //如果没找到，就选最小的size
        }
        return list[i]
    }

    //升序 按照高度
    class CameraAscendSizeComparatorForHeight :
        Comparator<Camera.Size> {
        override fun compare(
            lhs: Camera.Size,
            rhs: Camera.Size
        ): Int {
            return if (lhs.height == rhs.height) {
                0
            } else if (lhs.height > rhs.height) {
                1
            } else {
                -1
            }
        }
    }

    fun equalRate(s: Camera.Size, rate: Float): Boolean {
        val r = s.width.toFloat() / s.height.toFloat()
        return Math.abs(r - rate) <= 0.03
    }

    //降序
    class CameraDropSizeComparator :
        Comparator<Camera.Size> {
        override fun compare(
            lhs: Camera.Size,
            rhs: Camera.Size
        ): Int {
            return when {
                lhs.width == rhs.width -> {
                    0
                }
                lhs.width < rhs.width -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }
    }

    //升序
    class CameraAscendSizeComparator :
        Comparator<Camera.Size> {
        override fun compare(
            lhs: Camera.Size,
            rhs: Camera.Size
        ): Int {
            return when {
                lhs.width == rhs.width -> {
                    0
                }
                lhs.width > rhs.width -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }
    }

    /**
     * 打印支持的previewSizes
     *
     * @param params
     */
    fun printSupportPreviewSize(params: Camera.Parameters) {
        val previewSizes =
            params.supportedPreviewSizes
        for (i in previewSizes.indices) {
            val size = previewSizes[i]
            Log.d(TAG, ">> printSupportPreviewSize: " + size.width + ", " + size.height)
        }
    }

    /**
     * 打印支持的pictureSizes
     *
     * @param params
     */
    fun printSupportPictureSize(params: Camera.Parameters) {
        val pictureSizes =
            params.supportedPictureSizes
        for (i in pictureSizes.indices) {
            val size = pictureSizes[i]
            Log.d(TAG, ">> printSupportPictureSize: " + size.width + ", " + size.height)
        }
    }

    /**
     * 打印支持的聚焦模式
     *
     * @param params
     */
    fun printSupportFocusMode(params: Camera.Parameters) {
        val focusModes = params.supportedFocusModes
        for (mode in focusModes) {
            Log.i(TAG, ">> focusModes--$mode");
        }
    }

    /**
     * 打开闪关灯
     *
     * @param mCamera
     */
    fun turnLightOn(mCamera: Camera?) {
        if (mCamera == null) {
            return
        }
        val parameters = mCamera.parameters ?: return
        val flashModes =
            parameters.supportedFlashModes
                ?: // Use the screen as a flashlight (next best thing)
                return
        // Check if camera flash exists
        val flashMode = parameters.flashMode
        if (Camera.Parameters.FLASH_MODE_ON != flashMode) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                mCamera.parameters = parameters
            } else {
            }
        }
    }

    /**
     * 自动模式闪光灯
     *
     * @param mCamera
     */
    fun turnLightAuto(mCamera: Camera?) {
        if (mCamera == null) {
            return
        }
        val parameters = mCamera.parameters ?: return
        val flashModes =
            parameters.supportedFlashModes
                ?: // Use the screen as a flashlight (next best thing)
                return
        // Check if camera flash exists
        val flashMode = parameters.flashMode
        if (Camera.Parameters.FLASH_MODE_AUTO != flashMode) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                mCamera.parameters = parameters
            } else {
            }
        }
    }

    /**
     * 关闭闪光灯
     *
     * @param mCamera
     */
    fun turnLightOff(mCamera: Camera?) {
        if (mCamera == null) {
            return
        }
        val parameters = mCamera.parameters ?: return
        val flashModes =
            parameters.supportedFlashModes
        val flashMode = parameters.flashMode
        // Check if camera flash exists
        if (flashModes == null) {
            return
        }
        if (Camera.Parameters.FLASH_MODE_OFF != flashMode) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                mCamera.parameters = parameters
            } else {
            }
        }
    }
}