package com.promeets.android.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

public class FileUtils {
    /**
     * Get file from file path
     *
     * @param filePath
     * @return
     */
    public static File getFileByPath(String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * Check from file path
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * Check from File instance
     *
     * @param file
     * @return
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static Hashtable<String, Object> getFileInfo(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Hashtable<String, Object> value = new Hashtable<String, Object>();
                    value.put("path", Environment.getExternalStorageDirectory() + "/" + split[1]);
                    value.put("size", (int)new File((String)value.get("path")).length());
                    value.put("mime", "application/octet-stream");

                    return value;
                }
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
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isNewGooglePhotosUri(uri)) {
                Hashtable<String, Object> value = getDataColumn(context, uri, null, null);
                Bitmap bitmap;
                try {
                    InputStream input = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(input);
                    File file = File.createTempFile("sendbird", ".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new BufferedOutputStream(new FileOutputStream(file)));
                    value.put("path", file.getAbsolutePath());
                    value.put("size", (int)file.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return value;
            } else {
                return getDataColumn(context, uri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Hashtable<String, Object> value = new Hashtable<String, Object>();
            value.put("path", uri.getPath());
            value.put("size", (int)new File((String)value.get("path")).length());
            value.put("mime", "application/octet-stream");

            return value;
        }

        return null;
    }

    private static Hashtable<String, Object> getDataColumn(Context context, Uri uri, String selection,
                                                           String[] selectionArgs) {

        Cursor cursor = null;
        String COLUMN_DATA = MediaStore.MediaColumns.DATA;
        String COLUMN_MIME = MediaStore.MediaColumns.MIME_TYPE;
        String COLUMN_SIZE = MediaStore.MediaColumns.SIZE;

        String[] projection = {
                COLUMN_DATA,
                COLUMN_MIME,
                COLUMN_SIZE
        };

        try {
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
            } catch(IllegalArgumentException e) {
                COLUMN_MIME = "mimetype"; // DownloadProvider.sAppReadableColumnsArray.COLUMN_MIME_TYPE
                projection[1] = COLUMN_MIME;
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(COLUMN_DATA);
                String path = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(COLUMN_MIME);
                String mime = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(COLUMN_SIZE);
                int size = cursor.getInt(column_index);

                Hashtable<String, Object> value = new Hashtable<String, Object>();
                if(path == null) path = "";
                if(mime == null) mime = "application/octet-stream";

                value.put("path", path);
                value.put("mime", mime);
                value.put("size", size);

                return value;
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
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
    private static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    public static void saveToFile(File file, String data) throws IOException {
        File tempFile = File.createTempFile("sendbird", "temp");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data.getBytes());
        fos.close();

        if(!tempFile.renameTo(file)) {
            throw new IOException("Error to rename file to " + file.getAbsolutePath());
        }
    }

    public static String loadFromFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        int read;
        while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }
}
