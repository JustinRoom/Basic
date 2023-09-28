package jsc.org.lib.basic.object;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * SharedPreferences
 * @author jsc
 */
public final class SharePreferenceObj {

    private static class SingletonHolder {
        private static final SharePreferenceObj INSTANCE = new SharePreferenceObj();
    }
    private SharedPreferences mSharePreferences;

    private SharePreferenceObj() {
    }

    public static SharePreferenceObj getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context, String fileName) {
        mSharePreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defValue) {
        requiredInitialized();
        return mSharePreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        requiredInitialized();
        return mSharePreferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        requiredInitialized();
        return mSharePreferences.getFloat(key, defValue);
    }

    public long getLong(String key, long defValue) {
        requiredInitialized();
        return mSharePreferences.getLong(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        requiredInitialized();
        return mSharePreferences.getBoolean(key, defValue);
    }

    public void saveString(String key, String value) {
        requiredInitialized();
        mSharePreferences.edit()
                .putString(key, value)
                .apply();
    }

    public void saveInt(String key, int value) {
        requiredInitialized();
        mSharePreferences.edit()
                .putInt(key, value)
                .apply();
    }

    public void saveFloat(String key, float value) {
        requiredInitialized();
        mSharePreferences.edit()
                .putFloat(key, value)
                .apply();
    }

    public void saveLong(String key, long value) {
        requiredInitialized();
        mSharePreferences.edit()
                .putLong(key, value)
                .apply();
    }

    public void saveBoolean(String key, boolean value) {
        requiredInitialized();
        mSharePreferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    public boolean has(String key) {
        requiredInitialized();
        return mSharePreferences.contains(key);
    }

    public void delete(String key) {
        requiredInitialized();
        mSharePreferences.edit()
                .remove(key)
                .apply();
    }

    public void clear() {
        requiredInitialized();
        mSharePreferences.edit()
                .clear()
                .apply();
    }

    public void deleteLike(String likeKey) {
        requiredInitialized();
        Map<String, ?> all = mSharePreferences.getAll();
        for (String key : all.keySet()) {
            if (key.contains(likeKey)) {
                delete(key);
            }
        }
    }

    private void requiredInitialized() {
        if (mSharePreferences == null)
            throw new IllegalStateException("Please call init first.");
    }
}