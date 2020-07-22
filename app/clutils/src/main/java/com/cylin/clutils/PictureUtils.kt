package com.cylin.clutils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import java.io.File

/**
 *  取得相片的工具, 將通用的 intent 定義在這裡
 *  主要使用三種：
 *  請求相簿的 intent
 *  請求相機的 intent
 *  請求內建圖片編輯器的 intent
 *
 *  也包含 Path Uri 互轉的工具
 */
object PictureUtil {

    const val TAKE_PHOTO_REQUEST_CODE = 103

    const val GALLERY_REQUEST_CODE = 104

    const val CROP_REQUEST_CODE = 105

    /**
     * provider path, 必須與 xml/file_paths 裡面的 path 相同
     */
    private const val FILE_PATH = "/image/"

    private const val IMAGE_NAME = "image.jpg"

    /**
     * provider name, 必須與AndroidManifest裡面的 authorities 相同
     */
    private const val PROVIDER = ".fileProvider"

    /**
     * 拍照
     *
     * 若不指定儲存地點, 可直接於 onActivityResult 取得 data
     * 若設定指定 Uri, onActivityResult 會收到 null data
     * 要手動去指定的 Uri 讀取圖片, 但相片編輯器可能無法取得該圖片權限
     */
    @JvmStatic
    fun dispatchTakePictureIntent(activity: Activity, fragment: Fragment? = null) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        val list: List<ResolveInfo> = activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if (list.isNotEmpty()) {

            // Create the File where the photo should go
            // Continue only if the File was successfully created
            getFileUri(activity).apply {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, this)
            }
            // Launching the Intent
            if (fragment == null) {
                activity.startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE)
            } else {
                fragment.startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE)
            }
        }
    }

    /**
     * 從相簿選擇圖片
     */
    @JvmStatic
    fun dispatchPickFromGalleryIntent(activity: Activity, fragment: Fragment? = null) {

        // Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        // Launching the Intent
        if (fragment == null) {
            activity.startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } else {
            fragment.startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }
    }

    /**
     * 使用內建相片編輯功能裁切圖片
     *
     * @param sourceUri 圖片來源路徑, scheme 不限
     * @param targetUri 儲存路徑, scheme 限定 "file:"
     */
    fun dispatchCropPicture(activity: Activity, sourceUri: Uri, targetUri: Uri, fragment: Fragment? = null) {

        val intent = Intent("com.android.camera.action.CROP")
        // 輸入圖片來源
        intent.setDataAndType(sourceUri, "image/*")

        intent.putExtra("crop", "true")
        // 設定儲存地點的 Uri, 必須使用絕對路徑的 Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)

        intent.addFlags(
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 350)
        intent.putExtra("outputY", 350)
        intent.putExtra("scale", true)

        // 不能直接將圖片返回, 圖檔很大會出事
        intent.putExtra("return-data", false)

        val list: List<ResolveInfo> = activity.packageManager.queryIntentActivities(intent, 0)

        if (list.isNotEmpty()) {
            // Launching the Intent
            if (fragment == null) {
                activity.startActivityForResult(intent, CROP_REQUEST_CODE)
            } else {
                fragment.startActivityForResult(intent, CROP_REQUEST_CODE)
            }
        }
    }

    /**
     * 輸入 File instance 取得 File 的 Uri
     *
     * @param context context
     * @param file custom file or use default
     */
    fun getFileUri(context: Context, file: File = getImageFile(context)): Uri {

        // 這是File的 path string: /data/user/0/packageName/files/file/image123.jpg
        // 這是Uri 的 path string: /file_path/image123.jpg
        return FileProvider.getUriForFile(
            context,
            context.packageName + PROVIDER,
            file
        )
    }

    /**
     * 取得 File instance
     *
     * @param context context
     * @param imageName fileName, with ".jpg"
     */
    fun getImageFile(context: Context, imageName: String = IMAGE_NAME): File {

        val filePath = "${context.filesDir}$FILE_PATH$imageName"
        val outputFile = File(filePath)

        outputFile.parentFile?.let {
            if (!it.exists()) {
                val mkdir = it.mkdir()
            }
        }

        return outputFile
    }

    /**
     *
     * @param context context
     * @param dirName 資料夾名稱
     * @param imgFileName 檔案名稱
     * @return 返回絕對路徑的 Uri
     */
    fun getAbsolutePathUri(
        context: Context,
        dirName: String = "cropDir",
        imgFileName: String = "cropImage.jpg"
    ): Uri {

        return Uri.fromFile(
            File(context.getExternalFilesDir(dirName), "/$imgFileName")
        )
    }
}
