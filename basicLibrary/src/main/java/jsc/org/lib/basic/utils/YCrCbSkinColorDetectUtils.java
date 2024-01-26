package jsc.org.lib.basic.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

/**
 * RGB转换为Ycbcr公式：
 * Y = 0.257*R+0.564*G+0.098*B+16
 * Cb = -0.148*R-0.291*G+0.439*B+128
 * Cr = 0.439*R-0.368*G-0.071*B+128
 * <p>
 * Ycbcr转换为RGB公式：
 * R = 1.164*(Y-16)+1.596*(Cr-128)
 * G = 1.164*(Y-16)-0.392*(Cb-128)-0.813*(Cr-128)
 * B =1.164*(Y-16)+2.017*(Cb-128)
 */
public final class YCrCbSkinColorDetectUtils {

    public static void _RGB2YCrCb(int color, float[] v3c) {
        if (v3c == null || v3c.length < 3) return;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        float y = .257f * r + .564f * g + .098f * b + 16;
        v3c[0] = y;
        v3c[1] = .439f * r - .368f * g - .071f * b + 128;
        v3c[2] = -.148f * r - .291f * g + .439f * b + 128;
    }

    public static Bitmap yCrCbSkinDetect(Bitmap src) {
        //new float[]{133, 173}, new float[]{77, 127}
        return yCrCbSkinDetect(src, new float[]{135, 160}, new float[]{80,115});
    }

    public static Bitmap yCrCbSkinDetect(Bitmap src, float[] cr, float[] cb) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        float[] v3c = new float[3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = src.getPixel(x, y);
                _RGB2YCrCb(color, v3c);
                if (v3c[1] >= cr[0] && v3c[1] <= cr[1]
                        && v3c[2] >= cb[0] && v3c[2] <= cb[1]) {
                    target.setPixel(x, y, color);
                } else {
                    target.setPixel(x, y, Color.argb(Color.alpha(color), 0xFF, 0xFF, 0xFF));
                }
            }
        }
        return target;
    }

    public static float yCrCbSkinColorRatio(Bitmap src, Rect area) {
        //new float[]{133, 173}, new float[]{77, 127}
        return yCrCbSkinColorRatio(src, area, new float[]{135, 160}, new float[]{80,115});
    }

    public static float yCrCbSkinColorRatio(Bitmap src, Rect area, float[] cr, float[] cb) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (area == null) {
            area = new Rect(0, 0, width, height);
        }
        float[] v3c = new float[3];
        int count = 0;
        for (int x = area.left; x < area.right; x++) {
            for (int y = area.top; y < area.bottom; y++) {
                int color = src.getPixel(x, y);
                _RGB2YCrCb(color, v3c);
                if (v3c[1] >= cr[0] && v3c[1] <= cr[1]
                        && v3c[2] >= cb[0] && v3c[2] <= cb[1]) {
                    count++;
                }
            }
        }
        return count * 1.0f / (area.width() * area.height());
    }
}
