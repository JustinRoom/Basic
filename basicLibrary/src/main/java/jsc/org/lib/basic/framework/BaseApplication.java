package jsc.org.lib.basic.framework;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.List;

import jsc.org.lib.basic.object.LocalFileManager;
import jsc.org.lib.basic.object.LoggerImpl;
import jsc.org.lib.basic.object.SharePreferenceObj;
import jsc.org.lib.basic.widget.imitate.ImitateToast;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoggerImpl.getInstance().init(this, getExternalFilesDir("logs"), false);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                LoggerImpl.getInstance().t(null, e, true);
            }
        });
        SharePreferenceObj.getInstance().init(this, getPackageName() + ".data");
        LocalFileManager.getInstance().init(this);
        ImitateToast.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LoggerImpl.getInstance().unInit();
        ImitateToast.unInit();
    }

    /**
     * 退出app
     */
    public final void exitApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (manager != null) {
                List<ActivityManager.AppTask> appTasks = manager.getAppTasks();
                for (ActivityManager.AppTask task : appTasks) {
                    task.finishAndRemoveTask();
                }
            }
        }
        exitAppViolently();
    }

    public final void exitAppViolently() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
