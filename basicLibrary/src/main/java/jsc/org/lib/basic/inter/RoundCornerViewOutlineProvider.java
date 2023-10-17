package jsc.org.lib.basic.inter;

import android.graphics.Outline;
import android.graphics.Path;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.IntDef;

public final class RoundCornerViewOutlineProvider extends ViewOutlineProvider {

    private static final int MASK = 0xFF;
    public static final int TOP_LEFT = MASK >> 6;
    public static final int TOP_RIGHT = TOP_LEFT << 2;
    public static final int BOTTOM_RIGHT = TOP_LEFT << 4;
    public static final int BOTTOM_LEFT = TOP_LEFT << 6;

    int corner;
    float radius;

    public RoundCornerViewOutlineProvider(int corner, float radius) {
        this.corner = corner;
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }

        //这个里边传入的Path只支持LineTo(), 二介及以上的贝塞尔曲线都会报错
        Path path = createOutlinePath(view.getWidth(), view.getHeight());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path);
        } else {
            outline.setConvexPath(path);
        }
    }

    public Path createOutlinePath(int width, int height) {
        Path path = new Path();
        path.moveTo(0, radius);
        if ((MASK & corner) == TOP_LEFT) {
            path.quadTo(0, 0, radius, 0);
        } else {
            path.lineTo(0, 0);
            path.lineTo(width - radius, 0);
        }

        if ((MASK & corner) == TOP_RIGHT) {
            path.quadTo(width, 0, width, radius);
        } else {
            path.lineTo(width, 0);
            path.lineTo(width, height - radius);
        }

        if ((MASK & corner) == BOTTOM_RIGHT) {
            path.quadTo(width, height, width - radius, height);
        } else {
            path.lineTo(width, height);
            path.lineTo(radius, height);
        }

        if ((MASK & corner) == BOTTOM_LEFT) {
            path.quadTo(0, height, 0, height - radius);
        } else {
            path.lineTo(0, height);
        }
        path.close();
        return path;
    }
}
