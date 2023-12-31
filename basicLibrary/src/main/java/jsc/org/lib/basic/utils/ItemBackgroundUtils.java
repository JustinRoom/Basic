package jsc.org.lib.basic.utils;

import android.util.TypedValue;
import android.view.View;

public final class ItemBackgroundUtils {

    public static void applyItemBackgroundRipple(View view) {
        TypedValue typedValue = new TypedValue();
        // I used getActivity() as if you were calling from a fragment.
        // You just want to call getTheme() on the current activity, however you can get it
        view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        // it's probably a good idea to check if the color wasn't specified as a resource
        if (typedValue.resourceId != 0) {
            view.setBackgroundResource(typedValue.resourceId);
        } else {
            // this should work whether there was a resource id or not
            view.setBackgroundColor(typedValue.data);
        }
    }

    public static void applyItemBackgroundBorderlessRipple(View view) {
        TypedValue typedValue = new TypedValue();
        // I used getActivity() as if you were calling from a fragment.
        // You just want to call getTheme() on the current activity, however you can get it
        view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true);
        // it's probably a good idea to check if the color wasn't specified as a resource
        if (typedValue.resourceId != 0) {
            view.setBackgroundResource(typedValue.resourceId);
        } else {
            // this should work whether there was a resource id or not
            view.setBackgroundColor(typedValue.data);
        }
    }
}
