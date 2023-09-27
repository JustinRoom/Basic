package jsc.org.lib.basic.widget.imitate;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class ImitateDialogManager {

    private final static Map<String, Stack<BaseImitateDialog>> cache = new HashMap<>();

    public static void offer(AppCompatActivity activity, BaseImitateDialog instance) {
        offer(activity.getClass().getName(), instance);
    }

    public static void offer(String type, BaseImitateDialog instance) {
        Stack<BaseImitateDialog> stack = cache.get(type);
        if (stack == null) {
            stack = new Stack<>();
            cache.put(type, stack);
        }
        stack.push(instance);
    }

    public static void pop(AppCompatActivity activity, BaseImitateDialog instance) {
        pop(activity.getClass().getName(), instance);
    }

    public static void pop(String type, BaseImitateDialog instance) {
        Stack<BaseImitateDialog> stack = cache.get(type);
        if (stack != null && !stack.isEmpty()) {
            BaseImitateDialog temp = stack.peek();
            if (instance == temp) {
                stack.pop();
            }
        }
    }

    public static boolean cancel(AppCompatActivity activity) {
        return cancel(activity.getClass().getName());
    }

    public static boolean cancel(String type) {
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

    public static void clear(AppCompatActivity activity) {
        clear(activity.getClass().getName());
    }

    public static void clear(String type) {
        cache.remove(type);
    }
}
