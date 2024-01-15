package jsc.org.lib.basic.object;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;

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
            List<String> directories = getUSBTempRootDirectories(mContext);
            if (!directories.isEmpty() && listener != null) {
                listener.in(directories.get(0));
            }
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

    /**
     * 获取 U 盘临时根目录（一体机会在临时目录下再创建多个包含 "udisk" 的目录，所以临时目录并不是 U 盘真正的根目录）
     */
    public static List<String> getUSBTempRootDirectories(Context context) {
        List<String> directories = new ArrayList<>();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class<StorageManager> storageManagerClass = StorageManager.class;
            String[] paths = (String[]) storageManagerClass.getMethod("getVolumePaths").invoke(storageManager);
            if (paths == null) {
                paths = new String[]{};
            }
            for (String path : paths) {
                Object volumeState = storageManagerClass.getMethod("getVolumeState", String.class).invoke(storageManager, path);
                //路劲包含 internal 一般是内部存储，例如 /mnt/internal_sd，需要排除
                if (!path.contains("emulated")
                        && !path.contains("internal")
                        && Environment.MEDIA_MOUNTED.equals(volumeState)) {
                    directories.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directories;
    }
}