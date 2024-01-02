package jsc.org.lib.basic.utils;

import android.os.Build;
import android.os.FileUtils;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public final class MyFileUtils {

    /**
     * 效率最快的复制文件
     *
     * @param from
     * @param to
     */
    public static boolean nioTransferCopy(File from, @NonNull File to) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(from);
            if (to.isDirectory()) {
                os = new FileOutputStream(new File(to, from.getName()));
            } else {
                os = new FileOutputStream(to);
            }
            in = is.getChannel();
            out = os.getChannel();
            in.transferTo(0, in.size(), out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (in != null) {
                    in.close();
                }
                if (os != null) {
                    os.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean copyFile(@NonNull File file, File folder) {
        if (!file.exists()
                || !folder.isDirectory()) {
            return false;
        }
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        File dst = new File(folder, file.getName());
        try {
            if (dst.createNewFile()) {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(dst);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FileUtils.copy(fis, fos);
                } else {
                    byte[] data = new byte[10 * 1024];
                    int len;
                    while ((len = fis.read(data)) > 0) {
                        fos.write(data, 0, len);
                        fos.flush();
                    }
                }
                fos.close();
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void writeToFile(String dstPathName, String content, boolean append) {
        if (!TextUtils.isEmpty(dstPathName)) {
            File dst = new File(dstPathName);
            File folder = dst.getParentFile();
            if (folder == null) return;
            if (!folder.exists()) {
                boolean mr = folder.mkdirs();
            }
            try {
                if (!dst.exists()) {
                    boolean cr = dst.createNewFile();
                }
                writeToFile(dst, content, append);
            } catch (IOException ignore) {

            }
        }
    }

    public static void writeToFile(File dst, String content, boolean append) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(dst, append);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(File file, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static String readFromFile(@NonNull File file, boolean wrap) {
        if (!file.exists() || file.isDirectory()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                if (wrap) {
                    builder.append("\n");
                }
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static boolean saveContent(String content, File file) {
        if (content == null) return false;
        return saveDate(content.getBytes(StandardCharsets.UTF_8), file);
    }

    public static String readContent(File file) {
        byte[] data = readData(file);
        return data == null ? "" : new String(data, 0, data.length, StandardCharsets.UTF_8);
    }

    public static boolean saveDate(byte[] data, File file) {
        if (file == null
                || data == null
                || data.length == 0) return false;
        try {
            if (file.exists()) {
                boolean dr = file.delete();
            }
            if (file.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data, 0, data.length);
                fos.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readData(File file) {
        if (file == null) return null;
        if (!file.exists()) return null;
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte[] data = new byte[length];
            if (fis.read(data, 0, length) > 0) {
                return data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteFile(File file) {
        if (file.isFile()) {
            boolean dr = file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                boolean dr = file.delete();
                return;
            }
            for (File child : files) {
                deleteFile(child);
            }
            boolean dr = file.delete();
        }
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
