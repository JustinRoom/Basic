package jsc.org.lib.basic.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


public final class StringUtils {

    public static final String[] CHINA_NATION = new String[]{
            "汉族", "满族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族",
            "壮族", "布依族", "侗族", "瑶族", "白族", "土家族", "哈尼族", "哈萨克族",
            "傣族", "黎族", "傈僳族", "佤族", "畲族", "高山族", "拉祜族", "水族",
            "东乡族", "纳西族", "景颇族", "柯尔克孜族", "土族", "达斡尔族", "仫佬族", "羌族",
            "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族",
            "塔吉克族", "怒族", "乌孜别克族", "俄罗斯族", "鄂温克族", "德昂族", "保安族", "裕固族",
            "京族", "塔塔尔族", "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族",
            "穿青人"
    };
    public final static String WEB_URL_REGEX = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
    public final static String PATTERN_ENGLISH_CHARACTERS = "[a-zA-Z]";
    public final static String[] NUMBER_TEXT = new String[]{"①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩"};

    public static boolean isWebUrl(String src) {
        Pattern pattern = Pattern.compile(WEB_URL_REGEX);
        return pattern.matcher(src).matches();
    }

    public static long toLong(String obj) {
        try {
            return TextUtils.isEmpty(obj) ? 0L : Long.parseLong(obj);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int toInt(String str) {
        try {
            return TextUtils.isEmpty(str) ? 0 : Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float toFloat(String str) {
        try {
            return TextUtils.isEmpty(str) ? 0.0f : Float.parseFloat(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double toDouble(String str) {
        try {
            return TextUtils.isEmpty(str) ? 0.0D : Double.parseDouble(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断字符串是否有值
     *
     * @param str 输入字符串
     * @return true 无值，false 有值
     */
    public static boolean isBlank(String str) {
        return str == null
                || str.length() == 0
                || str.trim().length() == 0
                || str.trim().equalsIgnoreCase("null");
    }

    /**
     * 获取非空字符
     *
     * @param str 输入字符串
     * @return true 无值，false 有值
     */
    public static String notNullStr(String str) {
        return isBlank(str) ? "" : str;
    }

    /**
     * 字符串中是否包含英文字符
     */
    public static boolean containEnglishCharacters(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile(PATTERN_ENGLISH_CHARACTERS);
        return pattern.matcher(str).find();
    }

    public static int compare(String number1, String number2) {
        BigDecimal decimal1 = new BigDecimal(number1);
        BigDecimal decimal2 = new BigDecimal(number2);
        return decimal1.compareTo(decimal2);
    }
}