package com.jsc.basic;

import jsc.org.lib.basic.framework.BaseApplication;
import jsc.org.lib.basic.object.Locator;
import jsc.org.lib.basic.widget.imitate.ImitateToast;

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ImitateToast.init(this, null);
        Locator.getInstance().init(this);
    }
}
