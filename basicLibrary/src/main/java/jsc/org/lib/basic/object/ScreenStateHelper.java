package jsc.org.lib.basic.object;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 息屏、亮屏、解锁监听辅助类
 * @author jsc
 */
public final class ScreenStateHelper {

    private static ScreenStateHelper instance = null;
    private final Object lock = new Object();
    private Context mContext;
    private BroadcastReceiver receiver = null;
    private final List<StateListener> listeners = new ArrayList<>();

    public static ScreenStateHelper getInstance() {
        if (instance == null) {
            instance = new ScreenStateHelper();
        }
        return instance;
    }

    private ScreenStateHelper() {
    }

    public void register(Context context) {
        mContext = context;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_ON.equals(action)) { // 亮屏
                    screenOn(context);
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                    screenOff();
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                    userPresent();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(receiver, filter);
    }

    public void unregister() {
        listeners.clear();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
            receiver = null;
            mContext = null;
        }
    }

    public void addListener(@NonNull StateListener listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    public void removeListener(@NonNull StateListener listener) {
        synchronized (lock) {
            listeners.remove(listener);
        }
    }

    private void screenOn(Context context) {
        synchronized (lock) {
            KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean isUnLocked = !mKeyguardManager.inKeyguardRestrictedInputMode();
            for (StateListener l : listeners) {
                l.onScreenOn(isUnLocked);
            }
        }
    }

    private void userPresent() {
        synchronized (lock) {
            for (StateListener l : listeners) {
                l.onUserPresent();
            }
        }
    }

    private void screenOff() {
        synchronized (lock) {
            for (StateListener l : listeners) {
                l.onScreenOff();
            }
        }
    }

    /**
     * 获取screen状态
     */
    public static boolean isScreenOn(Context mContext) {
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        return manager.isScreenOn();
    }

    /**
     * 是否解锁。(只在亮屏情况下此判断才有效)
     *
     * @param mContext
     * @return
     */
    public static boolean isUnLocked(Context mContext) {
        if (isScreenOn(mContext)) {
            KeyguardManager mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
            //当前的屏幕锁有五种设置，分别是没有设置屏幕锁，滑动解锁，图案解锁，PIN码解锁，密码解锁。
            //如果没有设置屏幕锁，返回值会一直为FALSE。
            //如果用户设置了屏幕锁(包括后四种锁中的任何一种)，屏幕不亮时返回TRUE;屏幕亮时，解锁前返回TRUE，解锁后返回FALSE。
            return !flag;
        }
        return false;
    }

    public interface StateListener {// 返回给调用者屏幕状态信息

        /**
         * 亮屏
         * @param isUnLocked 是否解锁
         */
        void onScreenOn(boolean isUnLocked);

        /**
         * 息屏
         */
        void onScreenOff();

        /**
         * 解锁
         */
        void onUserPresent();
    }
}