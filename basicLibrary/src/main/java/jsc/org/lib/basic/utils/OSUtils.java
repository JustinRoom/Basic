package jsc.org.lib.basic.utils;

import java.lang.reflect.InvocationTargetException;

public final class OSUtils {

    public static boolean isHarmonyOS() {
        try {
            Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
            Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
            return osBrand != null && "Harmony".equalsIgnoreCase(osBrand.toString());
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException ignore) {

        }
        return false;
    }
}
