package jsc.org.lib.basic.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Utils {

    private final static String[] HEX_CODE = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public static String md5_32(String str) {
        return TextUtils.isEmpty(str) ? "" : md5_32(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5_16(String str) {
        return TextUtils.isEmpty(str) ? "" : md5_16(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5_16(File file) {
        String md5_32 = md5_32(file);
        return TextUtils.isEmpty(md5_32) ? "" : md5_32.substring(8, 24);
    }

    public static String md5_32(File file) {
        try {
            if (file.isFile()) {
                FileInputStream stream = new FileInputStream(file);
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] buf = new byte[8192];
                int len = 0;
                while ((len = stream.read(buf)) > 0) {
                    digest.update(buf, 0, len);
                }
                stream.close();
                return toHexString(digest.digest());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String md5_16(byte[] bytes) {
        String md5_32 = md5_32(bytes);
        return TextUtils.isEmpty(md5_32) ? "" : md5_32.substring(8, 24);
    }

    public static String md5_32(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            //返回32位md5码
            return toHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }
}