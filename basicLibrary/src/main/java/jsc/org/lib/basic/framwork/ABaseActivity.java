package jsc.org.lib.basic.framwork;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Stack;

public abstract class ABaseActivity extends AppCompatActivity {

    private boolean firstLoad = true;

    protected boolean screenshot() {
        return true;
    }

    protected boolean keepScreenOn() {
        return false;
    }

    public abstract View initContentView();

    public final int getWindowContentId() {
        return android.R.id.content;
    }

    /**
     * Only call once.
     */
    public abstract void onLazyLoad();

    public void onReLazyLoad() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!screenshot()) {//防截屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (keepScreenOn()) {//防息屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        View view = initContentView();
        if (view != null) {
            setContentView(view);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstLoad) {
            firstLoad = false;
            onLazyLoad();
        } else {
            onReLazyLoad();
        }
    }

    @Override
    public void onBackPressed() {
        if (!backFragment()) {
            super.onBackPressed();
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>fragment manager start<<<<<<<<<<<<<<<<<<<<<<<
    private final Stack<String> fragmentStack = new Stack<>();

    /**
     * fragment回退
     *
     * @return true有fragment(或childFragment)
     */
    public final boolean backFragment() {
        while (!fragmentStack.isEmpty()) {
            String fragmentTag = fragmentStack.pop();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (fragment == null) {
                continue;
            }

            if (fragment instanceof ABaseFragment) {
                ABaseFragment f = (ABaseFragment) fragment;
                if (!f.backChildFragment()) {
                    //remove self
                    f.onBackPressed();
                    removeFragment(f);
                    f.onRemoved();
                }
            } else {
                removeFragment(fragment);
            }
            return true;
        }
        return false;
    }

    public final void addFragment(@IdRes int containerViewId, Fragment fragment, String tag) {
        addFragment(containerViewId, fragment, tag, true);
    }

    public final void addFragment(@IdRes int containerViewId, Fragment fragment, String tag, boolean intoFragmentStack) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(containerViewId, fragment, tag).commit();
            if (intoFragmentStack) {
                pushIntoFragmentStack(tag);
            }
        }
    }

    public final void pushIntoFragmentStack(String tag) {
        fragmentStack.push(tag);
    }

    public final void removeFragment(String tag) {
        removeFragment(getSupportFragmentManager().findFragmentByTag(tag));
    }

    public final void removeFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>fragment manager end<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstLoad", firstLoad);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCallbacksAndMessages();
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>Handler start<<<<<<<<<<<<<<<<<<<<<<<
    private Handler handler = null;

    public final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    handleMyMessage(msg);
                }
            };
        }
        return handler;
    }

    public void clearCallbacksAndMessages() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void handleMyMessage(@NonNull Message message) {

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>Handler end<<<<<<<<<<<<<<<<<<<<<<<

    public final void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public final void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view == null ? getWindow().getDecorView().getWindowToken() : view.getWindowToken(), 0);
        }
    }

    /**
     * Close soft input keyboard.
     */
    public final void hideInputMethod() {
        hideInputMethod(null);
    }

    public final void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, 0x8F));
        } else {
            vibrator.vibrate(200);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public final void vibrateClick() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }

    public final boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public final void showToast(@Nullable CharSequence txt) {
        if (!TextUtils.isEmpty(txt)) {
            Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }
}
