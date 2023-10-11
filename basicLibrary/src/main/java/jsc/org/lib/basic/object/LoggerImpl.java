package jsc.org.lib.basic.object;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class LoggerImpl {

    private static class SingletonHolder {
        private static final LoggerImpl INSTANCE = new LoggerImpl();
    }

    private final Object lock = new Object();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.US);
    private SharedPreferences mPreferences = null;
    private FileWriter mFileWriter = null;
    private boolean initialized = false;
    private boolean isDebugModel;
    private String versionInfo = "";
    private String rootPath = "";
    private File mLogFile = null;
    private String lastDateStr = "";
    private long mMaxFileSize = 0L;

    private LoggerImpl() {
    }

    public static LoggerImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context, @NonNull File folder, boolean debug) {
        //默认限制4MB大小
        long size = 4 * 1024 * 1024;
        init(context, folder, size, debug);
    }

    public void init(Context context, @NonNull File folder, long maxFileSize, boolean debug) {
        if (!initialized) {
            initialized = true;
            this.mMaxFileSize = maxFileSize;
            this.isDebugModel = debug;
            if (!folder.exists()) {
                boolean mr = folder.mkdirs();
            }
            rootPath = folder.getPath();
            try {
                mPreferences = context.getSharedPreferences("logger.data", Context.MODE_PRIVATE);
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                versionInfo = packInfo.versionName + "  " + packInfo.versionCode;
                lastDateStr = mPreferences.getString("lastDateStr", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            initCurLogFile();
        }
    }

    public void unInit() {
        try {
            initialized = false;
            closeFileWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The log files folder's path.
     *
     * @return directory path
     */
    public String getDirectory() {
        return rootPath;
    }

    /**
     * Delete all log files.
     */
    public void clearLogs() {
        try {
            closeFileWriter();
            lastDateStr = "";
            if (mPreferences != null) {
                mPreferences.edit().putString(lastDateStr, "").apply();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files = new File(rootPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("log");
            }
        });
        if (files != null && files.length > 0) {
            for (File f : files) {
                boolean dr = f.delete();
            }
        }
    }

    public void v(String content) {
        v(null, content);
    }

    public void d(String content) {
        d(null, content);
    }

    public void i(String content) {
        i(null, content);
    }

    public void w(String content) {
        w(null, content);
    }

    public void e(String content) {
        e(null, content);
    }

    public void t(Throwable th) {
        t(null, th);
    }

    public void v(String tag, String content) {
        v(tag, content, true);
    }

    public void d(String tag, String content) {
        d(tag, content, true);
    }

    public void i(String tag, String content) {
        i(tag, content, true);
    }

    public void w(String tag, String content) {
        w(tag, content, true);
    }

    public void e(String tag, String content) {
        e(tag, content, true);
    }

    public void t(String tag, Throwable th) {
        t(tag, th, true);
    }

    public void v(String tag, String content, boolean saveToLocal) {
        if (isDebugModel) {
            Log.v(tag, content);
            return;
        }
        if (saveToLocal) {
            log(Log.VERBOSE, tag, content);
        }
    }

    public void d(String tag, String content, boolean saveToLocal) {
        if (isDebugModel) {
            Log.d(tag, content);
            return;
        }
        if (saveToLocal) {
            log(Log.DEBUG, tag, content);
        }
    }

    public void i(String tag, String content, boolean saveToLocal) {
        if (isDebugModel) {
            Log.i(tag, content);
            return;
        }
        if (saveToLocal) {
            log(Log.INFO, tag, content);
        }
    }

    public void w(String tag, String content, boolean saveToLocal) {
        if (isDebugModel) {
            Log.w(tag, content);
            return;
        }
        if (saveToLocal) {
            log(Log.WARN, tag, content);
        }
    }

    public void e(String tag, String content, boolean saveToLocal) {
        if (isDebugModel) {
            Log.e(tag, content);
            return;
        }
        if (saveToLocal) {
            log(Log.ERROR, tag, content);
        }
    }

    public void t(String tag, Throwable th, boolean saveToLocal) {
        if (isDebugModel) {
            Log.e(tag, th.toString());
            return;
        }
        if (saveToLocal) {
            log(tag, th);
        }
    }

    private void log(int type, String tag, String content) {
        if (!initialized) return;
        String typeStr = "";
        if (type == Log.VERBOSE) {
            typeStr = "VERBOSE";
        } else if (type == Log.DEBUG) {
            typeStr = "DEBUG";
        } else if (type == Log.INFO) {
            typeStr = "INFO";
        } else if (type == Log.WARN) {
            typeStr = "WARN";
        } else if (type == Log.ERROR) {
            typeStr = "ERROR";
        }
        if (!TextUtils.isEmpty(content)) {
            StringBuilder builder = new StringBuilder();
            Date date = new Date();
            appendDateAndVersionIfN(builder, date);
            dateFormat.applyPattern("HH:mm:ss");
            builder.append(dateFormat.format(new Date()))
                    .append(" [").append(typeStr).append("]")
                    .append("[").append(TextUtils.isEmpty(tag) ? "Logger" : tag).append("]-> ")
                    .append(content).append("\n");
            synchronized (lock) {
                write(builder.toString());
            }
        }
    }

    private void log(String tag, Throwable th) {
        if (!initialized) return;
        if (th != null) {
            StringBuilder builder = new StringBuilder();
            Date date = new Date();
            appendDateAndVersionIfN(builder, date);
            dateFormat.applyPattern("HH:mm:ss");
            builder.append(dateFormat.format(new Date()))
                    .append(" [ERROR]")
                    .append("[").append(TextUtils.isEmpty(tag) ? "Logger" : tag).append("]-> ")
                    .append(th.toString());
            StackTraceElement[] traceElements = th.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                builder.append("\n\tat ").append(traceElement.toString());
            }
            builder.append("\n");
            synchronized (lock) {
                write(builder.toString());
            }
        }
    }

    private void appendDateAndVersionIfN(StringBuilder builder, Date date) {
        dateFormat.applyPattern("yyyy年MM月dd日");
        String curDateStr = dateFormat.format(date);
        if (!lastDateStr.equals(curDateStr)) {
            lastDateStr = curDateStr;
            builder.append("\n")
                    .append("Date:").append(curDateStr).append("\n")
                    .append("Vers:").append(versionInfo).append("\n");
            if (mPreferences != null) {
                mPreferences.edit().putString("lastDateStr", lastDateStr).apply();
            }
        }
    }

    private void initCurLogFile() {
        try {
            closeFileWriter();
            String date = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            int index = -1;
            while (true) {
                index++;
                mLogFile = new File(rootPath, String.format(Locale.US, "%s_%03d.txt", date, index));
                if (!mLogFile.exists()) {
                    break;
                }
                if (mLogFile.length() < mMaxFileSize) {
                    break;
                }
            }
            if (!mLogFile.exists()) {
                //create an new file. 创建一个新文件
                lastDateStr = "";
                if (mPreferences != null) {
                    mPreferences.edit().putString(lastDateStr, "").apply();
                }
                boolean cr = mLogFile.createNewFile();
            }
            writeHeaderInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDeviceInfo() {
        return "Android Version:" + Build.VERSION.RELEASE + "\n"
                + "Sdk Version:" + Build.VERSION.SDK_INT + "\n"
                + "Brand:" + Build.BRAND + "\n"
                + "Manufacturer:" + Build.MANUFACTURER + "\n"
                + "Model:" + Build.MODEL + "\n"
                + "CPU ABI:" + Build.CPU_ABI + "\n";
    }

    private void writeHeaderInfo() {
        String content = "Create Time:" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.US).format(new Date()) + "\n";
        write(content);
        write(getDeviceInfo());
    }

    private void write(String content) {
        try {
            if (mFileWriter == null) {
                mFileWriter = new FileWriter(mLogFile, true);
            }
            mFileWriter.write(content);
            mFileWriter.flush();
            if (mLogFile.length() >= mMaxFileSize) {
                initCurLogFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeFileWriter() throws IOException {
        if (mFileWriter != null) {
            mFileWriter.close();
            mFileWriter = null;
        }
    }
}
