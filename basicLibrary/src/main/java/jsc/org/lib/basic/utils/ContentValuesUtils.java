package jsc.org.lib.basic.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public final class ContentValuesUtils {

    public static String queryPath(Context context, Uri uri) {
        String[] projection = new String[]{
                MediaStore.MediaColumns._ID,
                MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                if (new File(path).exists()) {
                    return path;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return null;
    }
}
