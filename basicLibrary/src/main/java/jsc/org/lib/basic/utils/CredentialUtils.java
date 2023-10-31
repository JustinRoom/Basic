package jsc.org.lib.basic.utils;

import java.util.HashMap;
import java.util.Map;

public final class CredentialUtils {

    private final static Map<String, String> map = new HashMap<>();

    static {
        map.put("1", "居民身份证");
        map.put("A", "香港居民身份证");
        map.put("6", "外籍护照");
        map.put("9", "其他");
        map.put("B", "澳门居民身份证");
        map.put("C", "台湾居住有效身份证明");
    }

    public static String findCredentialNameByCode(String code) {
        for (String c : map.keySet()) {
            if (c.equals(code)) {
                return map.get(c);
            }
        }
        return "";
    }
}
