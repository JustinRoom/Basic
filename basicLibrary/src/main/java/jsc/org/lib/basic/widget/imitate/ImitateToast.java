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

import androidx.annotation.NonNull;

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
    private WindowManager.LayoutParams mDefaultLayoutParams = null;
    private WindowManager.LayoutParams mCustomLayoutParams = null;
    private int mLayoutParamsModel = 0;//0 default, 1 custom

    private ImitateToast() {

    }

    private static ImitateToast getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void register(Context context, int gravity, int x, int y, OnViewInitializeListener listener) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            mDefaultLayoutParams = createLayoutParams(gravity, x, y);
            mCustomLayoutParams = createLayoutParams(gravity, x, y);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
            mView = new TextView(context.getApplicationContext());
            mView.setGravity(Gravity.CENTER);
            mView.setTextColor(Color.WHITE);
            mView.setBackgroundColor(0xBF000000);
            mView.setPadding(padding, padding / 3, padding, padding / 3);
            mView.setMinWidth(context.getResources().getDisplayMetrics().widthPixels / 4);
            mView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels * 4 / 5);
            mView.setMaxLines(32);
            ViewOutlineUtils.applyHorizontalEllipticOutline(mView);
            if (listener != null) {
                listener.onViewInitialize(mView);
            }
        }
    }

    private WindowManager.LayoutParams createLayoutParams(int gravity, int x, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = R.style.ImitateWindowAnimStyle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.gravity = gravity;
        params.x = x;
        params.y = y;
        return params;
    }

    private void unregister() {
        cancelDisappear(true);
        mView = null;
        mWindowManager = null;
        mDefaultLayoutParams = null;
        mCustomLayoutParams = null;
    }

    private void showTextInDefaultModel(CharSequence text, long delay) {
        if (mWindowManager == null)
            throw new IllegalStateException("Please register first.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mView.getContext()))
            throw new IllegalStateException("No permission for Settings.ACTION_MANAGE_OVERLAY_PERMISSION.");
        cancelDisappear(mLayoutParamsModel == 1);
        mView.setText(text);
        if (mView.getParent() == null) {
            mWindowManager.addView(mView, mDefaultLayoutParams);
            mLayoutParamsModel = 0;
            ObjectAnimator.ofPropertyValuesHolder(
                            mView,
                            PropertyValuesHolder.ofFloat(View.ALPHA, .75f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f, 1.0f))
                    .setDuration(250L)
                    .start();
        }
        scheduleDisappear(delay);
    }

    private void showTextInCustomModel(CharSequence text, int gravity, int x, int y, long delay) {
        if (mWindowManager == null)
            throw new IllegalStateException("Please register first.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mView.getContext()))
            throw new IllegalStateException("No permission for Settings.ACTION_MANAGE_OVERLAY_PERMISSION.");
        cancelDisappear(mLayoutParamsModel == 0);
        mView.setText(text);
        boolean gravityChanged = mCustomLayoutParams.gravity != gravity;
        boolean xChanged = mCustomLayoutParams.x != x;
        boolean yChanged = mCustomLayoutParams.y != y;
        boolean changed = gravityChanged || xChanged || yChanged;
        if (changed) {
            mCustomLayoutParams.gravity = gravity;
            mCustomLayoutParams.x = x;
            mCustomLayoutParams.y = y;
        }
        if (mView.getParent() == null) {
            mWindowManager.addView(mView, mCustomLayoutParams);
            mLayoutParamsModel = 1;
            ObjectAnimator.ofPropertyValuesHolder(
                            mView,
                            PropertyValuesHolder.ofFloat(View.ALPHA, .75f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f, 1.0f))
                    .setDuration(250L)
                    .start();
        } else if (changed) {
            mWindowManager.updateViewLayout(mView, mCustomLayoutParams);
        }
        scheduleDisappear(delay);
    }

    private void scheduleDisappear(long delay) {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mWindowManager.removeView(mView);
                    mTimer = null;
                }
            }, delay);
        }
    }

    private void cancelDisappear(boolean detach) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (detach && mWindowManager != null
                && mView != null
                && mView.getParent() != null) {
            mWindowManager.removeView(mView);
        }
    }

    public static void init(Context context, OnViewInitializeListener listener) {
        init(context, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 48, listener);
    }

    public static void init(Context context, int gravity, int x, int y, OnViewInitializeListener listener) {
        ImitateToast.getInstance().register(context, gravity, x, y, listener);
    }

    public static void show(CharSequence text) {
        show(text, 2_500L);
    }

    public static void show(CharSequence text, long time) {
        ImitateToast.getInstance().showTextInDefaultModel(text, time);
    }

    public static void show(CharSequence text, int gravity, int x, int y) {
        show(text, gravity, x, y, 2_500L);
    }

    public static void show(CharSequence text, int gravity, int x, int y, long time) {
        ImitateToast.getInstance().showTextInCustomModel(text, gravity, x, y, time);
    }

    public static void unInit() {
        ImitateToast.getInstance().unregister();
    }

    public interface OnViewInitializeListener {
        void onViewInitialize(@NonNull TextView view);
    }
}