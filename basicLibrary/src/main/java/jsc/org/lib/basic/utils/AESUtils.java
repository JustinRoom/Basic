package jsc.org.lib.basic.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public final class AESUtils {

    /**
     * AES 加密
     *
     * @param plaintext 待加密内容
     * @param keyStr    加密密码，长度：16 或 32 个字符
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String plaintext, String keyStr) {
        try {
            //设置为加密模式
            if (plaintext != null && plaintext.length() > 0) {
                Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, keyStr);
                if (cipher != null) {
                    byte[] bytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
                    // 将加密以后的数据进行Base64编码
                    return Base64.encodeToString(bytes, Base64.NO_WRAP);
                }
            }
        } catch (IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * AES 解密
     *
     * @param ciphertext 加密的密文 Base64 字符串
     * @param keyStr     解密的密钥，长度：16 或 32 个字符
     */
    public static String decrypt(String ciphertext, String keyStr) {
        try {
            //设置为解密模式
            if (ciphertext != null && ciphertext.length() > 0) {
                Cipher cipher = createCipher(Cipher.DECRYPT_MODE, keyStr);
                if (cipher != null) {
                    byte[] bytes = Base64.decode(ciphertext, Base64.NO_WRAP);
                    //执行解密操作
                    byte[] result = cipher.doFinal(bytes);
                    return new String(result, StandardCharsets.UTF_8);
                }
            }
        } catch (BadPaddingException
                | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Cipher createCipher(int opmode, String keyStr) {
        String cipherAlgorithm = "AES/PKCS5Padding";//PKCS5Padding每次生成一样的密文，iso10126每次生成不一样的密文(Android不支持)
        try {
            //创建密码器
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            //初始化为加密密码器
            cipher.init(opmode, createSecretKeySpec(keyStr));
            return cipher;
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create SecretKeySpec.
     */
    private static SecretKeySpec createSecretKeySpec(String keyStr) {
        if (keyStr.length() > 32)
            throw new IllegalArgumentException("The length of 'keyStr' is more than 32.");
        int length = keyStr.length() > 16 ? 32 : 16;
        String text = "0";
        // 获取密钥长度
        int strLen = keyStr.length();
        // 判断长度是否小于应有的长度
        if (strLen < length) {
            // 补全位数
            StringBuilder builder = new StringBuilder();
            // 将key添加至builder中
            builder.append(keyStr);
            // 遍历添加默认文本
            for (int i = 0; i < length - strLen; i++) {
                builder.append(text);
            }
            return new SecretKeySpec(builder.toString().getBytes(StandardCharsets.UTF_8), "AES");
        }
        return new SecretKeySpec(keyStr.getBytes(StandardCharsets.UTF_8), "AES");
    }
}