package jsc.org.lib.basic.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import androidx.annotation.NonNull;

/**
 * 避免临时创建大量的对象（可能会导致OutOfMemoryError）
 */
public final class NV212BitmapTransferObj {

    private RenderScript rs = null;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = null;
    private Type.Builder yuvType = null;
    private Type.Builder rgbaType = null;
    private int tw = 0;
    private int th = 0;
    private Bitmap outBitmap = null;
    private boolean destroyed = false;

    public NV212BitmapTransferObj(@NonNull Context context) {
        rs = RenderScript.create(context.getApplicationContext());
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        yuvType = new Type.Builder(rs, Element.U8(rs));
        rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs));
    }

    public Bitmap transfer(byte[] nv21, int width, int height) {
        if (destroyed || nv21 == null || nv21.length == 0) {
            return null;
        }

        yuvType.setX(nv21.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
        rgbaType.setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        //bitmap不可用高频创建
        if (outBitmap == null) {
            tw = width;
            th = height;
            outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            if (tw != width || th != height) {
                tw = width;
                th = height;
                outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
        }
        out.copyTo(outBitmap);

        in.destroy();
        out.destroy();
        return outBitmap;
    }

    public void destroy() {
        destroyed = true;
        if (rs != null) {
            rs.destroy();
            rs = null;
        }
        if (yuvToRgbIntrinsic != null) {
            yuvToRgbIntrinsic.destroy();
            yuvToRgbIntrinsic = null;
        }
    }
}
