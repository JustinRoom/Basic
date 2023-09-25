package jsc.org.lib.basic.object;

import android.content.Context;
import android.widget.Toast;

import jsc.org.lib.basic.utils.OSUtils;

public final class ToastImpl {

    private static ToastImpl instance = null;
    private Context context;
    private Toast mToast;
    private boolean isHarmonyOs = false;

    private ToastImpl() {
    }

    public static ToastImpl getInstance() {
        if (instance == null) {
            instance = new ToastImpl();
        }
        return instance;
    }

    public void init(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext();
            isHarmonyOs = OSUtils.isHarmonyOS();
        }
    }

    public boolean isHarmonyOs() {
        return isHarmonyOs;
    }

    public void show(CharSequence text) {
        if (isHarmonyOs) {
            //鸿蒙系统不能用静态的Toast，会崩溃
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
