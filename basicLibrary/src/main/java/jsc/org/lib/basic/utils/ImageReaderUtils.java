package jsc.org.lib.basic.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;

import androidx.annotation.NonNull;
import java.nio.ByteBuffer;
import jsc.org.lib.basic.bean.ImagePlane;

public final class ImageReaderUtils {

    public static final int YUV420P = 0;
    public static final int YUV420SP = 1;
    public static final int NV21 = 2;

    /**
     * @param image
     * @param nv21Bytes length = width * height * 3 / 2
     */
    public static void parseNV21FromImage(@NonNull Image image, byte[] nv21Bytes) {
        int width = image.getWidth();
        int height = image.getHeight();
        parseYuvDataFromImageByType(
                toImagePlanes(image),
                width,
                height,
                NV21,
                nv21Bytes);
    }

    public static void parseYuvDataFromImageByType(@NonNull ImagePlane[] mPlanes, int width, int height, int type, byte[] yuvOutput) {
        //目标数组的装填到的位置
        int dstIndex = 0;

        //临时存储uv数据的
        byte[] uBytes = new byte[width * height / 4];
        byte[] vBytes = new byte[width * height / 4];
        int uIndex = 0;
        int vIndex = 0;

        int len = mPlanes.length;

        for (int i = 0; i < len; i++) {
            ImagePlane imagePlane = mPlanes[i];
            int pixelsStride = imagePlane.pixelsStride;
            int rowStride = imagePlane.rowStride;

            int srcIndex = 0;
            if (i == 0) {
                //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
                for (int j = 0; j < height; j++) {
                    System.arraycopy(imagePlane.bytes, srcIndex, yuvOutput, dstIndex, width);
                    srcIndex += rowStride;
                    dstIndex += width;
                }
            } else if (i == 1) {
                //根据pixelsStride取相应的数据
                for (int j = 0; j < height / 2; j++) {
                    for (int k = 0; k < width / 2; k++) {
                        uBytes[uIndex++] = imagePlane.bytes[srcIndex];
                        srcIndex += pixelsStride;
                    }
                    if (pixelsStride == 2) {
                        srcIndex += rowStride - width;
                    } else if (pixelsStride == 1) {
                        srcIndex += rowStride - width / 2;
                    }
                }
            } else if (i == 2) {
                //根据pixelsStride取相应的数据
                for (int j = 0; j < height / 2; j++) {
                    for (int k = 0; k < width / 2; k++) {
                        vBytes[vIndex++] = imagePlane.bytes[srcIndex];
                        srcIndex += pixelsStride;
                    }
                    if (pixelsStride == 2) {
                        srcIndex += rowStride - width;
                    } else if (pixelsStride == 1) {
                        srcIndex += rowStride - width / 2;
                    }
                }
            }
        }

        //根据要求的结果类型进行填充
        switch (type) {
            case YUV420P:
                System.arraycopy(uBytes, 0, yuvOutput, dstIndex, uBytes.length);
                System.arraycopy(vBytes, 0, yuvOutput, dstIndex + uBytes.length, vBytes.length);
                break;
            case YUV420SP:
                for (int i = 0; i < vBytes.length; i++) {
                    yuvOutput[dstIndex++] = uBytes[i];
                    yuvOutput[dstIndex++] = vBytes[i];
                }
                break;
            case NV21:
                for (int i = 0; i < vBytes.length; i++) {
                    yuvOutput[dstIndex++] = vBytes[i];
                    yuvOutput[dstIndex++] = uBytes[i];
                }
                break;
        }
    }

    public static ImagePlane[] toImagePlanes(@NonNull Image image) {
        //获取源数据，如果是YUV格式的数据planes.length = 3
        //plane[i]里面的实际数据可能存在byte[].length <= capacity (缓冲区总大小)
        final Image.Plane[] planes = image.getPlanes();

        //尽快取出image里的数据，然后close掉
        int len = planes.length;
        ImagePlane[] mPlanes = new ImagePlane[len];
        for (int i = 0; i < len; i++) {
            ImagePlane imagePlane = new ImagePlane();
            imagePlane.pixelsStride = planes[i].getPixelStride();
            imagePlane.rowStride = planes[i].getRowStride();
            ByteBuffer buffer = planes[i].getBuffer();
            //如果pixelsStride==2，一般的Y的buffer长度=640*480，UV的长度=640*480/2-1
            //源数据的索引，y的数据是byte中连续的，u的数据是v向左移以为生成的，两者都是偶数位为有效数据
            imagePlane.bytes = new byte[buffer.capacity()];
            buffer.get(imagePlane.bytes);
            mPlanes[i] = imagePlane;
        }
        image.close();
        return mPlanes;
    }

    /**
     * YUV420 转化成 RGB
     *
     * @param yuv420sp
     * @param width
     * @param height
     * @param rgbOutput size = width * height
     */
    public static void decodeYUV420SP(byte[] yuv420sp, int width, int height, int[] rgbOutput) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) {
                    y = 0;
                }
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) {
                    r = 0;
                } else if (r > 262143) {
                    r = 262143;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 262143) {
                    g = 262143;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 262143) {
                    b = 262143;
                }
                rgbOutput[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }
}
