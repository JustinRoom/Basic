package jsc.org.lib.basic.utils;

import android.content.res.Resources;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

public final class ViewUtils {

    public static void disableCrossClick(@NonNull View v) {
        v.setClickable(true);
        v.setFocusable(true);
    }

    public static boolean viewIdEqual(View v, @IdRes int id) {
        return v.getId() == id;
    }

    public static boolean viewEqual(View view1, View view2) {
        return view1 == view2;
    }

    public static void applyToNavigationBarHeight(View view, int minHeight) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = Math.max(navigationBarHeight(view.getResources()), minHeight);
        view.setLayoutParams(params);
    }

    public static void applyToNavigationBarHeight(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = navigationBarHeight(view.getResources());
        view.setLayoutParams(params);
    }

    public static int statusBarHeight(@NonNull Resources resources) {
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int navigationBarHeight(@NonNull Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static String getEditText(EditText view) {
        return getEditText(view, true);
    }

    public static String getEditText(EditText view, boolean trim) {
        Editable editable = view.getText();
        String str = editable == null ? "" : editable.toString();
        return trim ? str.trim() : str;
    }
}
