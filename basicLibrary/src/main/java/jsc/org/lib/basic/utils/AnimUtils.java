package jsc.org.lib.basic.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public final class AnimUtils {

    /**
     * translate animation
     * @param view target view
     * @param from one of {"left","top","right","bottom"}
     * @param durationMillis duration
     */
    public static void translateBySelf(View view, String from, long durationMillis) {
        if (view == null || from == null) return;
        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        TranslateAnimation anim = null;
        switch (from) {
            case "left":
                anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                break;
            case "top":
                anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                break;
            case "right":
                anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                break;
            case "bottom":
                anim = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);
                break;
        }
        if (anim != null) {
            anim.setRepeatMode(Animation.RESTART);
            anim.setDuration(durationMillis);
            view.setAnimation(anim);
            anim.start();
        }
    }
}
