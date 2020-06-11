package com.cylin.clutils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;


public class AppUtils {

    private static String TAG = AppUtils.class.getSimpleName();

    public static String getApplicationName(@NonNull Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static void setupPermissions(Activity activity, String permission, int reqcode) {
        int result = ContextCompat.checkSelfPermission(activity.getApplication(), permission);

        if (result != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.i(TAG, ">> Permission denied: " + permission);
            ActivityCompat.requestPermissions(activity, new String[] { permission }, reqcode);
        } else {
            android.util.Log.i(TAG, ">> Permission has been denied by user: " + permission);
            activity.onRequestPermissionsResult(reqcode, new String[] { permission }, new int[] { PackageManager.PERMISSION_GRANTED });
        }
    }

    public static void setupPermissions(Activity activity, String[] permission, int reqcode) {
        int result = ContextCompat.checkSelfPermission(activity.getApplication(), permission[0]);

        if (result != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.i(TAG, ">> Permission denied: " + permission);
            ActivityCompat.requestPermissions(activity, permission, reqcode);
        } else {
            android.util.Log.i(TAG, ">> Permission has been denied by user: " + permission);
            activity.onRequestPermissionsResult(reqcode, permission, new int[] { PackageManager.PERMISSION_GRANTED });
        }
    }

    public static String downloadNewVersion(Context context, Uri source, int titleResId) {
        DownloadManager.Request request = new DownloadManager.Request(source);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);  // This will show notification on top when downloading the file.
        request.setTitle(context.getString(titleResId)); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setAllowedOverMetered(true);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, source.getLastPathSegment());  // Storage directory path
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
        return Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + source.getLastPathSegment();
    }

    public static void installPackage(Context context, String file) {
        Uri contentUri = FileProvider.getUriForFile(context, "com.excelutiontech.roadreporter.fileProvider", new File(file));
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        installIntent.setDataAndType(contentUri,"application/vnd.android.package-archive");
        context.startActivity(installIntent);
    }

    public static String getDownloadUriById(Context context, long enqueueId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(enqueueId);
        DownloadManager dm = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        Cursor c = dm.query(query);
        if (c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                return c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            }
        }
        return "";
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
    }

}
