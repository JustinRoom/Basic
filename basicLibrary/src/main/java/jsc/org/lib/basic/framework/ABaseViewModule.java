package jsc.org.lib.basic.framework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import jsc.org.lib.basic.inter.OnFragmentEventListener;
import jsc.org.lib.basic.utils.ViewUtils;

public abstract class ABaseViewModule<A extends AppCompatActivity> {

    private A activity;
    private OnFragmentEventListener listener = null;
    private ViewGroup parent;
    private View root;

    public abstract View bindView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public void init(@NonNull A activity,
                     @NonNull ViewGroup parent,
                     OnFragmentEventListener listener) {
        this.activity = activity;
        this.parent = parent;
        this.listener = listener;
    }

    @CallSuper
    public void show() {
        if (root == null) {
            root = bindView(activity.getLayoutInflater(), parent);
            if (root == null)
                throw new IllegalArgumentException("Invalid content view for module:" + getClass().getSimpleName());
            ViewUtils.disableCrossClick(root);
        }
        attach();
        if (root.getVisibility() != View.VISIBLE) {
            root.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (root != null && root.getParent() != null) {
            root.setVisibility(View.GONE);
        }
    }

    public final void attach() {
        attach(null);
    }

    public final void attach(ViewGroup.LayoutParams params) {
        if (root != null && root.getParent() == null) {
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            parent.addView(root, params);
        }
    }

    public final void detach() {
        if (root != null && root.getParent() != null) {
            parent.removeView(root);
        }
    }

    public final boolean isShowing() {
        return root != null
                && root.getParent() != null
                && root.getVisibility() == View.VISIBLE;
    }

    public void destroy() {

    }

    public final void dispatchEvent(int key, Bundle data) {
        if (listener != null) {
            listener.onEvent(key, data);
        }
    }

    public final A getActivity() {
        return activity;
    }
}