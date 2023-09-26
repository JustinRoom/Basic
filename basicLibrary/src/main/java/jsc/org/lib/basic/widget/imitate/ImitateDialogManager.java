package jsc.org.lib.basic.widget.imitate;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class ImitateDialogManager {

    private final static Map<String, Stack<BaseImitateDialog>> cache = new HashMap<>();

    public static void offer(String type, BaseImitateDialog instance) {
        Stack<BaseImitateDialog> stack = cache.get(type);
        if (stack == null) {
            stack = new Stack<>();
            cache.put(type, stack);
        }
        stack.push(instance);
    }

    public static void popup(String type, BaseImitateDialog instance) {
        Stack<BaseImitateDialog> stack = cache.get(type);
        if (stack != null && !stack.isEmpty()) {
            BaseImitateDialog temp = stack.peek();
            if (instance == temp) {
                stack.pop();
            }
        }
    }

    public static boolean popup(String type) {
        Stack<BaseImitateDialog> stack = cache.get(type);
        if (stack != null && !stack.isEmpty()) {
            BaseImitateDialog temp = stack.peek();
            if (temp.isCancelable()) {
                temp.cancel();
            }
            return true;
        }
        return false;
    }

    public static void clear(String type) {
        cache.remove(type);
    }
}
