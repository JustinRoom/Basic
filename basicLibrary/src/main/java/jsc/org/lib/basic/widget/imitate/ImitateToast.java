package jsc.org.lib.basic.widget.imitate;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
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

    public static class Builder {
        private int gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        private int x = 0;
        private int y = 48;
        private long time = 2_500L;

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public void show(CharSequence text) {
            ImitateToast.show(text, gravity, x, y, time);
        }
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
        cancel();
        mView = null;
        mWindowManager = null;
        layoutParams = null;
    }

    private void schedule(CharSequence text, int gravity, int x, int y, long time) {
        if (mWindowManager == null)
            throw new IllegalStateException("Please register first.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mView.getContext()))
            throw new IllegalStateException("No permission for Settings.ACTION_MANAGE_OVERLAY_PERMISSION.");
        cancel();
        mView.setText(text);
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.windowAnimations = R.style.ImitateWindowAnimStyle;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            layoutParams.setTitle("Toast");
            layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            layoutParams.gravity = gravity;
            layoutParams.y = y;
        }
        boolean isLayoutParamsChanged = needUpdateLayoutParams(gravity, x, y);
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

    private boolean needUpdateLayoutParams(int gravity, int x, int y) {
        boolean gravityChanged = layoutParams.gravity != gravity;
        boolean xChanged = layoutParams.x != x;
        boolean yChanged = layoutParams.y != y;
        layoutParams.gravity = gravity;
        layoutParams.x = x;
        layoutParams.y = y;
        return gravityChanged || xChanged || yChanged;
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

    public static void init(Context context) {
        ImitateToast.getInstance().register(context);
    }

    public static void show(CharSequence text) {
        show(text, 2_500L);
    }

    public static void show(CharSequence text, long time) {
        show(text, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 48, time);
    }

    public static void show(CharSequence text, int gravity, int x, int y, long time) {
        ImitateToast.getInstance().schedule(text, gravity, x, y, time);
    }

    public static void release() {
        ImitateToast.getInstance().releaseSource();
    }
}