package jsc.org.lib.basic.widget.imitate;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import jsc.org.lib.basic.R;
import jsc.org.lib.basic.utils.ViewOutlineUtils;

public final class ImitateToast {

    private static class SingletonHolder {
        private static final ImitateToast INSTANCE = new ImitateToast();
    }

    private WindowManager mWindowManager = null;
    private TextView mView = null;
    private Timer mTimer = null;
    private WindowManager.LayoutParams layoutParams = null;

    private ImitateToast() {

    }

    private static ImitateToast getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void register(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
            mView = new TextView(context.getApplicationContext());
            mView.setGravity(Gravity.CENTER);
            mView.setTextColor(Color.WHITE);
            mView.setBackgroundColor(0xBF000000);
            mView.setPadding(padding, padding / 3, padding, padding / 3);
            ViewOutlineUtils.applyHorizontalEllipticOutline(mView);
        }
    }

    private void releaseSource() {
        mView = null;
        mWindowManager = null;
        layoutParams = null;
    }

    private WindowManager.LayoutParams createLayoutParams(int gravity, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.ImitateWindowAnimStyle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = gravity;
        params.y = y;
        return params;
    }

    private void show(String text, int gravity, int y, long time) {
        cancel();
        mView.setText(text);
        if (layoutParams == null) {
            layoutParams = createLayoutParams(gravity, y);
        }
        boolean isLayoutParamsChanged = needUpdateLayoutParams(gravity, y);
        if (mView.getParent() == null) {
            mWindowManager.addView(mView, layoutParams);
            ObjectAnimator.ofPropertyValuesHolder(
                            mView,
                            PropertyValuesHolder.ofFloat(View.ALPHA, .75f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f, 1.0f))
                    .setDuration(250L)
                    .start();
        } else if (isLayoutParamsChanged) {
            mWindowManager.updateViewLayout(mView, layoutParams);
        }
        schedule(time);
    }

    private boolean needUpdateLayoutParams(int gravity, int y) {
        boolean gravityChanged = layoutParams.gravity != gravity;
        boolean yChanged = layoutParams.y != y;
        layoutParams.gravity = gravity;
        layoutParams.y = y;
        return gravityChanged || yChanged;
    }

    private void schedule(long time) {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mWindowManager.removeView(mView);
                    mTimer = null;
                }
            }, time);
        }
    }

    private void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, 2_500L);
    }

    public static void showToast(Context context, String text, long time) {
        showToast(context, text, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 48, time);
    }

    public static void showToast(Context context, String text, int gravity, int y) {
        showToast(context, text, gravity, y, 2_500L);
    }

    public static void showToast(Context context, String text, int gravity, int y, long time) {
        ImitateToast instance = ImitateToast.getInstance();
        instance.register(context);
        instance.show(text, gravity, y, time);
    }

    public static void release() {
        ImitateToast.getInstance().releaseSource();
    }
}