package jsc.org.lib.basic.utils;

import java.math.BigDecimal;

public final class FloatNumberUtils {

    public static int compareFloatNumber(String number1, String number2) {
        BigDecimal decimal1 = new BigDecimal(number1);
        BigDecimal decimal2 = new BigDecimal(number2);
        return decimal1.compareTo(decimal2);
    }

    public static String numberStr(String src) {
        if (src == null || src.length() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (c == '-' || c == '.' || (c >= '0' && c <= '9')) {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
