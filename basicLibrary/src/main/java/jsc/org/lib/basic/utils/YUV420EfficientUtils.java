package jsc.org.lib.basic.utils;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;

/**
 * CreateTime: 9:45 星期二
 *
 * @author jsc
 */
public class YUV420EfficientUtils {

    /**
     * @param nv21   源数据
     * @param width  源宽
     * @param height 源高
     * @param angle  旋转角度
     * @param output output size: width * height  * 3 / 2
     * @param size   new size
     */
    public static void rotateYUV420(byte[] nv21, int width, int height, int angle, byte[] output, @NonNull Point size) {
        angle = angle % 360;
        //调整为顺时针旋转
        angle = (360 + angle) % 360;
        size.x = width;
        size.y = height;
        if (angle % 180 > 0) {
            size.x = height;
            size.y = width;
        }
        switch (angle) {
            case 90:
                rotateYUV420_90(nv21, width, height, output);
                break;
            case 180:
                rotateYUV420_180(nv21, width, height, output);
                break;
            case 270:
                rotateYUV420_270(nv21, width, height, output);
                break;
            default:
                System.arraycopy(nv21, 0, output, 0, nv21.length);
                break;
        }
    }

    /**
     * @param nv21   源数据
     * @param width  源宽
     * @param height 源高
     * @param output output size: width * height  * 3 / 2
     */
    public static void rotateYUV420_270(byte[] nv21, int width, int height, byte[] output) {
        // Rotate the Y luma
        int i = 0;
        for (int x = width - 1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                output[i] = nv21[y * width + x];
                i++;
            }
        }// Rotate the U and V color components
        i = width * height;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < height / 2; y++) {
                output[i] = nv21[(width * height) + (y * width) + (x - 1)];
                i++;
                output[i] = nv21[(width * height) + (y * width) + x];
                i++;
            }
        }
    }

    /**
     * @param nv21   源数据
     * @param width  源宽
     * @param height 源高
     * @param output output size: width * height  * 3 / 2
     */
    public static void rotateYUV420_180(byte[] nv21, int width, int height, byte[] output) {
        int i = 0;
        int count = 0;
        for (i = width * height - 1; i >= 0; i--) {
            output[count] = nv21[i];
            count++;
        }
        for (i = width * height * 3 / 2 - 1; i >= width * height; i -= 2) {
            output[count++] = nv21[i - 1];
            output[count++] = nv21[i];
        }
    }

    /**
     * @param nv21   源数据
     * @param width  源宽
     * @param height 源高
     * @param output output size: width * height  * 3 / 2
     */
    public static void rotateYUV420_90(byte[] nv21, int width, int height, byte[] output) {
        int i = 0;
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                output[i] = nv21[y * width + x];
                i++;
            }
        }
        i = width * height * 3 / 2 - 1;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < height / 2; y++) {
                output[i] = nv21[(width * height) + (y * width) + x];
                i--;
                output[i] = nv21[(width * height) + (y * width) + (x - 1)];
                i--;
            }
        }
    }

    /**
     * @param src      源数据
     * @param width    源宽
     * @param height   源高
     * @param cropRect 裁剪区域
     * @param output   output size: cropRect.getWidth() * cropRect.getHeight()  * 3 / 2
     * @return
     */
    public static boolean cropYUV420(byte[] src, int width, int height, Rect cropRect, byte[] output) {
        if (cropRect != null) {
            return cropYUV420(src, width, height, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), output);
        }
        return false;
    }

    /**
     * NV21裁剪
     * 算法效率 3ms
     *
     * @param nv21       源数据
     * @param width      源宽
     * @param height     源高
     * @param left       顶点坐标
     * @param top        顶点坐标
     * @param cropWidth  裁剪后的宽
     * @param cropHeight 裁剪后的高
     * @param output     output size: cropWidth * cropHeight  * 3 / 2
     * @return false:失败, true:成功
     */
    public static boolean cropYUV420(byte[] nv21, int width, int height, int left, int top, int cropWidth, int cropHeight, byte[] output) {
        if (left > width
                || top > height
                || left + cropWidth > width
                || top + cropHeight > height
                || cropWidth % 4 > 0
                || cropHeight % 4 > 0) {
            return false;
        }
        //取偶
        int x = left / 4 * 4, y = top / 4 * 4;
        int w = cropWidth, h = cropHeight;
        int uv_index_dst = w * h - y / 2 * w;
        int uv_index_src = width * height + x;
        for (int i = y; i < y + h; i++) {
            System.arraycopy(nv21, i * width + x, output, (i - y) * w, w);//y内存块复制
            if (i % 2 == 0) {
                System.arraycopy(nv21, uv_index_src + (i >> 1) * width, output, uv_index_dst + (i >> 1) * w, w);//uv内存块复制
            }
        }
        return true;
    }

    /**
     * 剪切NV21数据并且镜像
     * 算法效率:
     * 1080x1920 14ms
     * 1280x720 6ms
     *
     * @param nv21       源数据
     * @param width      源宽
     * @param height     源高
     * @param left       顶点坐标
     * @param top        顶点坐标
     * @param cropWidth  裁剪后的宽
     * @param cropHeight 裁剪后的高
     * @param output     output size: cropWidth * cropHeight  * 3 / 2
     * @return false:失败, true:成功
     */
    public static boolean cropMirrorYUV420(byte[] nv21, int width, int height, int left, int top, int cropWidth, int cropHeight, byte[] output) {
        if (left > width
                || top > height
                || cropWidth % 4 > 0
                || cropHeight % 4 > 0) {
            return false;
        }
        //取偶
        int x = left, y = top;
        int w = cropWidth, h = cropHeight;
        int y_unit = w * h;
        int src_unit = width * height;
        int nPos = (y - 1) * width;
        int mPos;

        for (int i = y, len_i = y + h; i < len_i; i++) {
            nPos += width;
            mPos = src_unit + (i >> 1) * width;
            for (int j = x, len_j = x + w; j < len_j; j++) {
                output[(i - y + 1) * w - j + x - 1] = nv21[nPos + j];
                if ((i & 1) == 0) {
                    int m = y_unit + (((i - y) >> 1) + 1) * w - j + x - 1;
                    if ((m & 1) == 0) {
                        m++;
                        output[m] = nv21[mPos + j];
                        continue;
                    }
                    m--;
                    output[m] = nv21[mPos + j];
                }
            }
        }
        return true;
    }

    /**
     * 镜像NV21
     *
     * @param nv21   源数据
     * @param width  源宽
     * @param height 源高
     */
    public static void mirrorYUV420(byte[] nv21, int width, int height) {
        int i;
        int left, right;
        byte temp;
        int startPos = 0;
        // mirror Y
        for (i = 0; i < height; i++) {
            left = startPos;
            right = startPos + width - 1;
            while (left < right) {
                temp = nv21[left];
                nv21[left] = nv21[right];
                nv21[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
        // mirror U and V
        int offset = width * height;
        startPos = 0;
        for (i = 0; i < height / 2; i++) {
            left = offset + startPos;
            right = offset + startPos + width - 2;
            while (left < right) {
                temp = nv21[left];
                nv21[left] = nv21[right];
                nv21[right] = temp;
                left++;
                right--;
                temp = nv21[left];
                nv21[left] = nv21[right];
                nv21[right] = temp;
                left++;
                right--;
            }
            startPos += width;
        }
    }
}
