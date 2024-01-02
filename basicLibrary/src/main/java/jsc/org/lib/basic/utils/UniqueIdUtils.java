package jsc.org.lib.basic.utils;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

public final class UniqueIdUtils {

    private static String mDeviceUniqueCode = "";

    public static String defaultDeviceUniqueCode() {
        if (TextUtils.isEmpty(mDeviceUniqueCode)) {
            mDeviceUniqueCode = deviceUniqueCode(".yj", "sys_id");
        }
        return mDeviceUniqueCode;
    }

    /**
     * 获取设备唯一标识
     */
    public static String deviceUniqueCode(@NonNull String simpleFolderName,
                                          @NonNull String simpleFileName) {
        try {
            File root = Environment.getExternalStorageDirectory();
            File folder = new File(root, "Android" + File.separator + simpleFolderName);
            if (!folder.exists()) {
                boolean mr = folder.mkdirs();
            }
            File file = new File(folder, simpleFileName);
            String uniqueCode = file.exists() ? MyFileUtils.readFromFile(file, false) : "";
            if (uniqueCode.length() == 0) {
                uniqueCode = UUID.randomUUID().toString().toUpperCase(Locale.ROOT).replace("-", "");
                MyFileUtils.writeToFile(file, uniqueCode, false);
            }
            return uniqueCode;
        } catch (SecurityException se) {
            se.printStackTrace();
        }
        return "";
    }
}