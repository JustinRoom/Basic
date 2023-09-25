package jsc.org.lib.basic.inter;

import android.view.View;

import jsc.org.lib.basic.utils.AntiShakeUtils;

public class AntiShakeClickListener implements View.OnClickListener {

    boolean antiShakeEnable;

    public AntiShakeClickListener() {
        this(true);
    }

    public AntiShakeClickListener(boolean antiShakeEnable) {
        this.antiShakeEnable = antiShakeEnable;
    }

    @Override
    public final void onClick(View v) {
        if (!antiShakeEnable) {
            onAntiShakeClick(v);
            return;
        }
        if (AntiShakeUtils.isValidClick(v)) {
            onAntiShakeClick(v);
        }
    }

    public void onAntiShakeClick(View v) {

    }
}
