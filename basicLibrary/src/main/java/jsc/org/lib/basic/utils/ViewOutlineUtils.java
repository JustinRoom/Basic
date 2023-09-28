package jsc.org.lib.basic.utils;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;

import jsc.org.lib.basic.inter.RoundEdgeViewOutlineProvider;
import jsc.org.lib.basic.inter.RoundViewOutlineProvider;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class ViewOutlineUtils {

    public static void applyCircleOutline(View view) {
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() > 0 && view.getHeight() > 0) {
                    int min = Math.min(view.getWidth(), view.getHeight());
                    int l = (view.getWidth() - min) / 2;
                    int t = (view.getHeight() - min) / 2;
                    outline.setOval(l, t, l + min, t + min);
                }
            }
        });
        view.setClipToOutline(true);
    }

    public static void applyMinEdgeEllipticOutline(View view) {
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() > 0 && view.getHeight() > 0) {
                    float radius = Math.min(view.getWidth(), view.getHeight()) / 2.0f;
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            }
        });
        view.setClipToOutline(true);
    }

    public static void applyHorizontalEllipticOutline(View view) {
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() > 0 && view.getHeight() > 0) {
                    float radius = view.getHeight() / 2.0f;
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            }
        });
        view.setClipToOutline(true);
    }

    public static void applyVerticalEllipticOutline(View view) {
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() > 0 && view.getHeight() > 0) {
                    float radius = view.getWidth() / 2.0f;
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            }
        });
        view.setClipToOutline(true);
    }

    public static void applyRoundOutline2(View view, float dipRadius) {
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipRadius, view.getResources().getDisplayMetrics());
        applyRoundOutline1(view, radius);
    }

    public static void applyRoundOutline1(View view, final float radius) {
        view.setOutlineProvider(new RoundViewOutlineProvider(radius));
        view.setClipToOutline(true);
    }

    public static void applyRoundEdgOutline(View view, @RoundEdgeViewOutlineProvider.Edge int edge, final float radius) {
        view.setOutlineProvider(new RoundEdgeViewOutlineProvider(edge, radius));
        view.setClipToOutline(true);
    }

    public static void clearOutline(View view) {
        view.setOutlineProvider(null);
        view.setClipToOutline(false);
    }
}
