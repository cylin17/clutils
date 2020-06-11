package com.cylin.clutils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static android.util.Log.d;

public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    public static void takePhotos(Activity activity, int requestCode) {

        File outputImage = new File(getImagePath(),
                "tempImage" + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri imageUri = Uri.fromFile(outputImage);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    public static void pickImages(Activity activity, int requestCode, boolean multiple) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (multiple) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    /**
     * Need add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> to AndroidManifest.xml
     *
     * @param context
     * @param image
     * @return
     */
    public static String saveImage(@NonNull Context context, Bitmap image) {

        String uri = null;
        if (image != null) {
            String appName = AppUtils.getApplicationName(context);
            String fileName = appName + "_" + System.currentTimeMillis();
            uri = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, fileName, "Edit by " + appName);
        }
        return uri;
    }

    public static String saveImage2Gallery(@NonNull Context context, Bitmap bmp) {

        // 首先保存圖片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppUtils.getApplicationName(context);
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            // 通過IO Stream的方式來壓縮保存圖片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.flush();
            fos.close();

            // 把文件插入到系統圖庫
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            // 保存圖片後發送廣播通知更新數據庫
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            d(TAG, ">> saveImage2Gallery: image uri=" + uri.toString());
            if (isSuccess) {
                return uri.toString();
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap takeSnapshot(View v) {

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static String encodeImage(Bitmap bitmap) {

        String encodedImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            baos.close();
            encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (IllegalArgumentException e) {
            d(TAG, ">> encodeImage: IllegalArgumentException=" + e.getMessage());
        } catch (IOException e) {
            d(TAG, ">> encodeImage: IOException=" + e.getMessage());
        }
        return encodedImage;
    }

    public static Bitmap rotateBitmap(Bitmap original, float degrees) {
        int width = original.getWidth();
        int height = original.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
    }

    public static String saveImageView(Context context, ImageView view, Boolean isRotated) {

        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        if (isRotated) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(-90);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            bmp = rotateBitmap(bmp, -90);
        }

        String newUri = saveImage(context, bmp);
        d(TAG, ">> saveImageView: " + newUri);
        return newUri;

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

    public static File getImagePath() {

        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    public static String getRealPath(Context context, String imgUri) {
        return getRealPath(context, imgUri, false);
    }

    public static String getRealPath(Context context, String imgUri, boolean showLog) {
        if (showLog) android.util.Log.d(TAG, ">> before getRealPath: " + imgUri);
        String newPath = imgUri;
        if (imgUri.contains("content://com.android.")) {
            newPath = convertImagePath(context, imgUri);
        } else if (imgUri.contains("content://")) {
            newPath = ImageUtils.getRealPathFromURI(context, imgUri);
        }
        if (showLog) android.util.Log.d(TAG, ">> getRealPath: " + newPath);
        return newPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String convertImagePath(Context context, String imageUri) {
        Uri uri = Uri.parse(imageUri);
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        return "/storage/" + DocumentsContract.getDocumentId(uri).replace(":", "/");
    }

    public static String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        try {
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                String res = null;
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    res = cursor.getString(column_index);
                }
                return res;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static HashMap<String, Integer> getImageSize(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        HashMap imgSize = new HashMap<String, Integer>();
        imgSize.put("width", options.outWidth);
        imgSize.put("height", options.outHeight);
        return imgSize;
    }

}
