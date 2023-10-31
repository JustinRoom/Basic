package jsc.org.lib.basic.utils;

import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class NationUtils {

    private final static Map<String, String> map = new HashMap<>();

    static {
        map.put("01", "汉族");
        map.put("02", "蒙古族");
        map.put("03", "回族");
        map.put("04", "藏族");
        map.put("05", "维吾尔族");
        map.put("06", "苗族");
        map.put("07", "彝族");
        map.put("08", "壮族");
        map.put("09", "布依族");
        map.put("10", "朝鲜族");
        map.put("11", "满族");
        map.put("12", "侗族");
        map.put("13", "瑶族");
        map.put("14", "白族");
        map.put("15", "土家族");
        map.put("16", "哈尼族");
        map.put("17", "哈萨克族");
        map.put("18", "傣族");
        map.put("19", "黎族");
        map.put("20", "傈僳族");
        map.put("21", "佤族");
        map.put("22", "畲族");
        map.put("23", "高山族");
        map.put("24", "拉祜族");
        map.put("25", "水族");
        map.put("26", "东乡族");
        map.put("27", "纳西族");
        map.put("28", "景颇族");
        map.put("29", "柯尔克孜族");
        map.put("30", "土族");
        map.put("31", "达斡尔族");
        map.put("32", "仫佬族");
        map.put("33", "羌族");
        map.put("34", "布朗族");
        map.put("35", "撒拉族");
        map.put("36", "毛南族");
        map.put("37", "仡佬族");
        map.put("38", "锡伯族");
        map.put("39", "阿昌族");
        map.put("40", "普米族");
        map.put("41", "塔吉克族");
        map.put("42", "怒族");
        map.put("43", "乌孜别克族");
        map.put("44", "俄罗斯族");
        map.put("45", "鄂温克族");
        map.put("46", "崩龙族");
        map.put("47", "保安族");
        map.put("48", "裕固族");
        map.put("49", "京族");
        map.put("50", "塔塔尔族");
        map.put("51", "独龙族");
        map.put("52", "鄂伦春族");
        map.put("53", "赫哲族");
        map.put("54", "门巴族");
        map.put("55", "珞巴族");
        map.put("56", "基诺族");
        map.put("81", "穿青人");
        map.put("97", "其他");
        map.put("98", "外国血统中国籍人士");
    }

    @Nullable
    public static Pair<String, String> findNationByCode(String code) {
        for (String c : map.keySet()) {
            if (c.equals(code)) {
                return new Pair<>(c, map.get(c));
            }
        }
        return null;
    }

    @Nullable
    public static Pair<String, String> findNationByName(String name) {
        for (String c : map.keySet()) {
            String n = map.get(c);
            assert n != null;
            if (n.startsWith(name)) {
                return new Pair<>(c, n);
            }
        }
        return null;
    }
}
