package jsc.org.lib.basic.inter;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.IntDef;

public final class RoundEdgeViewOutlineProvider extends ViewOutlineProvider {

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    public @interface Edge {
    }

    int edge;
    float radius;

    public RoundEdgeViewOutlineProvider(@Edge int edge, float radius) {
        this.edge = edge;
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
        switch (edge) {
            case LEFT:
                int rightExtra = (int) (view.getHeight() + radius + 0.5f);
                outline.setRoundRect(0, 0, view.getWidth() + rightExtra, view.getHeight(), radius);
                break;
            case TOP:
                int bottomExtra = (int) (view.getHeight() + radius + 0.5f);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() + bottomExtra, radius);
                break;
            case RIGHT:
                int leftExtra = (int) (view.getHeight() + radius + 0.5f);
                outline.setRoundRect(-leftExtra, 0, view.getWidth(), view.getHeight(), radius);
                break;
            case BOTTOM:
                int topExtra = (int) (view.getHeight() + radius + 0.5f);
                outline.setRoundRect(0, -topExtra, view.getWidth(), view.getHeight(), radius);
                break;
        }
    }
}
