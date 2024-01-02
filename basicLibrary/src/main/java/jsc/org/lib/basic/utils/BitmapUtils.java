package jsc.org.lib.basic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class BitmapUtils {

    public static Bitmap rotateAroundCenter(Bitmap origin, float degrees) {
        if (degrees % 360 == 0) return origin;
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, width / 2.0f, height / 2.0f);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    public static void writeBytesToLocal(String pathName, byte[] bytes) {
        writeBytesToLocal(new File(pathName), bytes);
    }

    public static void writeBytesToLocal(File file, byte[] bytes) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmapToLocal(String pathName, Bitmap.CompressFormat format, Bitmap bitmap, int quality) {
        saveBitmapToLocal(new File(pathName), format, bitmap, quality);
    }

    public static void saveBitmapToLocal(File file, Bitmap.CompressFormat format, Bitmap bitmap, int quality) {
        if (bitmap == null) return;
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] copyPixels(@NonNull Bitmap bitmap) {
        int length = bitmap.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(length);
        bitmap.copyPixelsToBuffer(buf);
        byte[] source = buf.array();
        byte[] result = new byte[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        buf.clear();
        return result;
    }

    @NonNull
    public static byte[] bitmapToBytes(@NonNull Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, bos);
        byte[] byteArray = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static Bitmap decodeFile(String filePath,
                                    BitmapFactory.Options options,
                                    @IntRange(from = 0) int maxSize) {
        return decodeFile(filePath, options, maxSize, maxSize);
    }

    public static Bitmap decodeFile(String filePath,
                                    BitmapFactory.Options options,
                                    @IntRange(from = 0) int maxWidth,
                                    @IntRange(from = 0) int maxHeight) {
        if (filePath == null
                || filePath.length() == 0
                || !new File(filePath).exists()) {
            return null;
        }
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        while (true) {
            BitmapFactory.decodeFile(filePath, options);
            if (options.outWidth > maxWidth || options.outHeight > maxHeight) {
                options.inSampleSize = options.inSampleSize * 2;
            } else {
                break;
            }
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
