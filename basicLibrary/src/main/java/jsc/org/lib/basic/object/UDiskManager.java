package jsc.org.lib.basic.object;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * declare in 'AndroidManifest.xml':<br>
 * {@code
 *     <uses-feature android:name="android.hardware.usb.accessory" />
 *     <uses-feature android:name="android.hardware.usb.action.USB_DEVICE_DETACHED" />
 *     <uses-feature
 *         android:name="android.hardware.usb.host"
 *         android:required="true" />
 * }
 */
public final class UDiskManager {
    private Context mContext = null;
    private boolean registered = false;
    private OnPlugListener listener = null;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String path = intent.getData() == null ? null : intent.getData().getPath();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(action)) {
                if (listener != null) {
                    listener.out(path);
                }
            } else if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                if (listener != null) {
                    listener.in(path);
                }
            }
        }
    };

    public void bind(Context context, OnPlugListener listener) {
        this.mContext = context;
        this.listener = listener;
    }

    public void register() {
        if (!registered) {
            registered = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
            filter.setPriority(1000);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_SHARED);
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
            filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_NOFS);
            filter.addAction(Intent.ACTION_MEDIA_BUTTON);
            filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            filter.addDataScheme("file");
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        try {
            if (registered) {
                registered = false;
                mContext.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnPlugListener {

        void in(String path);

        void out(String path);
    }
}