package com.cylin.clutils.view.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import com.cylin.clutils.TAG
import java.io.File
import java.text.SimpleDateFormat

object AppUtils {

    fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

    fun setupPermissions(activity: Activity, permission: String, reqCode: Int) {
        val result = ContextCompat.checkSelfPermission(activity.application, permission)
        if (result != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, ">> Permission denied: $permission")
            ActivityCompat.requestPermissions(activity, arrayOf(permission), reqCode)
        } else {
            Log.i(TAG, ">> Permission has been denied by user: $permission")
            activity.onRequestPermissionsResult(
                reqCode,
                arrayOf(permission),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
        }
    }

    fun setupPermissions(activity: Activity, permission: Array<String?>, reqCode: Int) {
        val result = ContextCompat.checkSelfPermission(activity.application, permission[0]!!)
        if (result != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, ">> Permission denied: $permission")
            ActivityCompat.requestPermissions(activity, permission, reqCode)
        } else {
            Log.i(TAG, ">> Permission has been denied by user: $permission")
            activity.onRequestPermissionsResult(
                reqCode,
                permission,
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
        }
    }

    fun downloadNewVersion(context: Context, source: Uri, titleResId: Int): String {
        val request = DownloadManager.Request(source)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION) // This will show notification on top when downloading the file.
        request.setTitle(context.getString(titleResId)) // Title for notification.
        request.setVisibleInDownloadsUi(true)
        request.setAllowedOverMetered(true)
        request.setMimeType("application/vnd.android.package-archive")
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            source.lastPathSegment
        ) // Storage directory path
        (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(
            request
        )
        return Environment.getExternalStorageDirectory()
            .path + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + source.lastPathSegment
    }

    fun installPackage(context: Context, file: String?) {
        val contentUri = FileProvider.getUriForFile(
            context,
            "com.excelutiontech.roadreporter.fileProvider",
            File(file)
        )
        val installIntent = Intent(Intent.ACTION_VIEW)
        installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        context.startActivity(installIntent)
    }

    fun getDownloadUriById(context: Context, enqueueId: Long): String {
        val query = DownloadManager.Query()
        query.setFilterById(enqueueId)
        val dm =
            context.getSystemService(Activity.DOWNLOAD_SERVICE) as DownloadManager
        val c = dm.query(query)
        if (c.moveToFirst()) {
            val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                return c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            }
        }
        return ""
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    val currentTime: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())
}