package jsc.org.lib.basic.object;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import jsc.org.lib.basic.widget.imitate.ImitateToast;

public final class LocalFileManager {

    private static class SingletonHolder {
        private static final LocalFileManager INSTANCE = new LocalFileManager();
    }

    private Context applicationContext;

    private LocalFileManager() {
    }

    public static LocalFileManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void checkInitState() {
        if (applicationContext == null)
            throw new NullPointerException("Please init first.");
    }

    public File getInternalFilesDir(String name){
        checkInitState();
        File dir = applicationContext.getDir(name, Context.MODE_PRIVATE);
        createDirIfNotExist(dir);
        return dir;
    }

    public File getExternalFilesDir(String name) {
        checkInitState();
        File dir = applicationContext.getExternalFilesDir(name);
        if (dir != null) {
            createDirIfNotExist(dir);
        }
        return dir;
    }

    private void createDirIfNotExist(@NonNull File dir){
        if (!dir.exists()) {
            boolean mr = dir.mkdir();
        }
    }

    @NonNull
    public File getInternalFile(String dir, String fileName) {
        File folder = getInternalFilesDir(dir);
        return new File(folder, fileName);
    }

    @Nullable
    public File getExternalFile(String dir, String fileName) {
        File folder = getExternalFilesDir(dir);
        return folder == null ? null : new File(folder, fileName);
    }
}
