package com.lmtri.sharespace.helper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by lmtri on 8/17/2017.
 */

public class FileHelper {
    public static final String TAG = FileHelper.class.getSimpleName();

    public static Uri getImageContentUriFromFile(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static String getRealPathFromFileURI(Context context, Uri fileUri) {
        return fileUri.getPath();
    }

    public static String getRealPathFromContentURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {   // MediaStore (and general)
            if (isGooglePhotosUri(uri)) {   // Return the remote address
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {  // File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";  // MediaStore.Images.Media.DATA
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(
                    uri, projection,
                    selection, selectionArgs,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

//    @SuppressLint("NewApi")
//    public static String getRealPathFromURI_API19(Context context, Uri uri){
//        String filePath = "";
//        String wholeID = DocumentsContract.getDocumentId(uri);
//
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//
//        String[] column = { MediaStore.Images.Media.DATA };
//
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                column, sel, new String[]{ id }, null
//        );
//
//        int columnIndex = cursor.getColumnIndex(column[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//
//    @SuppressLint("NewApi")
//    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
//        String[] proj = { MediaStore.Images.Media.DATA };
//        String result = null;
//
//        CursorLoader cursorLoader = new CursorLoader(
//                context,
//                contentUri, proj, null, null, null);
//        Cursor cursor = cursorLoader.loadInBackground();
//
//        if(cursor != null){
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            result = cursor.getString(column_index);
//        }
//        return result;
//    }
//
//    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = context.getContentResolver()
//                .query(contentUri, proj, null, null, null);
//        int column_index
//                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
}
