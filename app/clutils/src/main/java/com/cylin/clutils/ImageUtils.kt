package com.cylin.clutils

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.cylin.clutils.view.utils.AppUtils
import java.io.*
import java.util.*

object ImageUtils {

    @JvmStatic
    fun takePhotos(activity: Activity, requestCode: Int) {
        val outputImage = File(
            imagePath,
            "tempImage" + ".jpg"
        )
        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val imageUri = Uri.fromFile(outputImage)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.toString())
        activity.startActivityForResult(intent, requestCode)
    }

    @JvmStatic
    fun pickImages(activity: Activity, requestCode: Int, multiple: Boolean) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        if (multiple) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    /**
     * Need add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission> to AndroidManifest.xml
     *
     * @param context
     * @param image
     * @return
     */
    @JvmStatic
    fun saveImage(context: Context, image: Bitmap?): String? {
        var uri: String? = null
        if (image != null) {
            val appName = AppUtils.getApplicationName(context)
            val fileName = appName + "_" + System.currentTimeMillis()
            uri = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                image,
                fileName,
                "Edit by $appName"
            )
        }
        return uri
    }

    @JvmStatic
    fun saveImage2Gallery(context: Context, bmp: Bitmap): String? {

        // 首先保存圖片
        val storePath = Environment.getExternalStorageDirectory()
            .absolutePath + File.separator + AppUtils.getApplicationName(context)
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            // 通過IO Stream的方式來壓縮保存圖片
            val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 70, fos)
            fos.flush()
            fos.close()

            // 把文件插入到系統圖庫
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            // 保存圖片後發送廣播通知更新數據庫
            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            Log.d(TAG, ">> saveImage2Gallery: image uri=$uri")
            return if (isSuccess) {
                uri.toString()
            } else {
                null
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun takeSnapshot(v: View): Bitmap {
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache(true)
        val b = Bitmap.createBitmap(v.drawingCache)
        v.isDrawingCacheEnabled = false
        return b
    }

    @JvmStatic
    fun encodeImage(bitmap: Bitmap): String {
        var encodedImage = ""
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            baos.close()
            encodedImage = Base64.encodeToString(b, Base64.NO_WRAP)
        } catch (e: IllegalArgumentException) {
            Log.d(
                TAG,
                ">> encodeImage: IllegalArgumentException=" + e.message
            )
        } catch (e: IOException) {
            Log.d(TAG, ">> encodeImage: IOException=" + e.message)
        }
        return encodedImage
    }

    @JvmStatic
    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap {
        val width = original.width
        val height = original.height
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(original, 0, 0, width, height, matrix, true)
    }

    @JvmStatic
    fun saveImageView(context: Context, view: ImageView, isRotated: Boolean): String? {
        view.buildDrawingCache()
        var bmp = view.drawingCache
        if (isRotated) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(-90);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            bmp = rotateBitmap(bmp, -90f)
        }
        val newUri = saveImage(context, bmp)
        Log.d(TAG, ">> saveImageView: $newUri")
        return newUri

//        OutputStream fOut = null;
//        try {
//            File root = getImagePath();
//            File sdImageMainDirectory = new File(root, "img_" + System.currentTimeMillis() +".jpg");
//            fOut = new FileOutputStream(sdImageMainDirectory);
//        } catch (Exception e) {
//            Toast.makeText(context, "Error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
//        }
//        try {
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//            fOut.flush();
//            fOut.close();
//        } catch (Exception e) {
//            d(TAG, ">> saveImage.error: " + e.getMessage());
//        }
    }

    @JvmStatic
    val imagePath: File
        get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

    @JvmStatic
    fun getRealPath(context: Context, imgUri: String): String? {
        return getRealPath(context, imgUri, false)
    }

    @JvmStatic
    fun getRealPath(context: Context, imgUri: String, showLog: Boolean): String? {
        if (showLog) Log.d(TAG, ">> before getRealPath: $imgUri")
        var newPath: String? = imgUri
        if (imgUri.contains("content://com.android.")) {
            newPath = convertImagePath(context, imgUri)
        } else if (imgUri.contains("content://")) {
            newPath = getRealPathFromURI(context, imgUri)
        }
        if (showLog) Log.d(TAG, ">> getRealPath: $newPath")
        return newPath
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    @JvmStatic
    fun convertImagePath(context: Context, imageUri: String?): String? {
        val uri = Uri.parse(imageUri)
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs =
                        arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            }
        }
        return "/storage/" + DocumentsContract.getDocumentId(uri).replace(":", "/")
    }

    @JvmStatic
    fun getRealPathFromURI(context: Context, contentURI: String?): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor =
            context.contentResolver.query(contentUri, null, null, null, null)
        return cursor.use { cursor ->
            if (cursor == null) {
                contentUri.path
            } else {
                var res: String? = null
                if (cursor.moveToFirst()) {
                    val columnIndex =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    res = cursor.getString(columnIndex)
                }
                res
            }
        }
    }

    @JvmStatic
    fun getImageSize(uri: Uri): HashMap<String, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(File(uri.path).absolutePath, options)
        val imgSize: HashMap<String, Int> = HashMap<String, Int>()
        imgSize["width"] = options.outWidth
        imgSize["height"] = options.outHeight
        return imgSize
    }
}