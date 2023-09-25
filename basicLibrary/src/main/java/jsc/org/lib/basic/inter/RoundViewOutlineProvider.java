package jsc.org.lib.basic.inter;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

public final class RoundViewOutlineProvider extends ViewOutlineProvider {

    float radius;

    public RoundViewOutlineProvider(float radius) {
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        if (view.getWidth() > 0 && view.getHeight() > 0) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
        }
    }
}
