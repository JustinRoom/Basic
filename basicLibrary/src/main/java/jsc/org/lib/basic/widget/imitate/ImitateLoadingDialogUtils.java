package jsc.org.lib.basic.widget.imitate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public final class ImitateLoadingDialogUtils {

    static Map<String, WeakReference<ImitateLoadingDialog>> map = new HashMap<>();

    public static void show(AppCompatActivity activity, @NonNull String key) {
        show(activity, false, false, key);
    }

    public static void show(AppCompatActivity activity, boolean cancelable, boolean cancelableTouchOutside, @NonNull String key) {
        ImitateLoadingDialog dialog = new ImitateLoadingDialog(activity);
        dialog.setCancelable(cancelable);
        dialog.setCancelableTouchOutside(cancelableTouchOutside);
        dialog.show();
        map.put(key, new WeakReference<>(dialog));
    }

    public static void dismiss(@NonNull String key) {
        WeakReference<ImitateLoadingDialog> weakReference = map.get(key);
        if (weakReference != null) {
            ImitateLoadingDialog dialog = weakReference.get();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
        map.remove(key);
    }

}
