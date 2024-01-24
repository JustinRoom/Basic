package jsc.org.lib.basic.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;


public final class StringUtils {

    public final static String WEB_URL_REGEX = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
    public final static String PATTERN_ENGLISH_CHARACTERS = "[a-zA-Z]";
    public final static String[] NUMBER_TEXT = new String[]{"①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩"};

    public static boolean isWebUrl(String src) {
        Pattern pattern = Pattern.compile(WEB_URL_REGEX);
        return pattern.matcher(src).matches();
    }

    public static long toLong(String src) {
        try {
            return TextUtils.isEmpty(src) ? 0L : Long.parseLong(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int toInt(String src) {
        try {
            return TextUtils.isEmpty(src) ? 0 : Integer.parseInt(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float toFloat(String src) {
        try {
            return TextUtils.isEmpty(src) ? 0.0f : Float.parseFloat(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double toDouble(String src) {
        try {
            return TextUtils.isEmpty(src) ? 0.0D : Double.parseDouble(src);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断字符串是否有值
     *
     * @param src 输入字符串
     * @return true 无值，false 有值
     */
    public static boolean isBlank(String src) {
        String s = trimStr(src);
        return s.length() == 0 || s.equalsIgnoreCase("null");
    }

    public static String trimStr(String src) {
        return src == null ? "" : src.trim();
    }

    /**
     * 字符串中是否包含英文字符
     */
    public static boolean containEnglishCharacters(String src) {
        if (TextUtils.isEmpty(src)) {
            return false;
        }
        Pattern pattern = Pattern.compile(PATTERN_ENGLISH_CHARACTERS);
        return pattern.matcher(src).find();
    }

    public static int compareFloatNumber(String number1, String number2) {
        try {
            BigDecimal decimal1 = new BigDecimal(number1);
            BigDecimal decimal2 = new BigDecimal(number2);
            return decimal1.compareTo(decimal2);
        } catch (NumberFormatException ignore) {

        }
        return -2;
    }

    public static String numberStr(String src, char[] include) {
        if (src == null || src.length() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (c >= '0' && c <= '9') {
                builder.append(c);
                continue;
            }
            for (char ch : include) {
                if (c == ch) {
                    builder.append(c);
                    break;
                }
            }
        }
        return builder.toString();
    }

    public boolean isEqual(String str1, String str2) {
        return !TextUtils.isEmpty(str1)
                && !TextUtils.isEmpty(str2)
                && str1.equals(str2);
    }
}