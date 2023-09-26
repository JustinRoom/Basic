package jsc.org.lib.basic.widget.imitate;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseImitateDialog {

    private AppCompatActivity activity = null;
    private FrameLayout root = null;
    private boolean initialized = false;
    private boolean cancelable = true;
    private boolean cancelableTouchOutside = true;
    private OnDismissListener onDismissListener = null;
    private OnCanceledListener onCanceledListener = null;

    public abstract void initContentView(@NonNull AppCompatActivity activity);

    public BaseImitateDialog(AppCompatActivity activity) {
        this.activity = activity;
        root = new FrameLayout(activity);
        root.setBackgroundColor(0x80666666);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public final boolean isCancelable() {
        return cancelable;
    }

    public final void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public final boolean isCancelableTouchOutside() {
        return cancelableTouchOutside;
    }

    public final void setCancelableTouchOutside(boolean cancelableTouchOutside) {
        this.cancelableTouchOutside = cancelableTouchOutside;
    }

    public final void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public final void setOnCanceledListener(OnCanceledListener onCanceledListener) {
        this.onCanceledListener = onCanceledListener;
    }

    @CallSuper
    public void show() {
        if (!initialized) {
            initialized = true;
            initContentView(activity);
        }
        attach();
        root.setEnabled(cancelable && cancelableTouchOutside);
    }

    @CallSuper
    public void dismiss() {
        detach();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(activity);
        }
    }

    @CallSuper
    public void cancel() {
        detach();
        if (onCanceledListener != null) {
            onCanceledListener.onCanceled(activity);
        }
    }

    private void attach() {
        if (root.getParent() == null) {
            ViewGroup parent = activity.findViewById(android.R.id.content);
            parent.addView(root, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ImitateDialogManager.offer(activity.getClass().getName(), this);
        }
    }

    private void detach() {
        if (root.getParent() != null) {
            ViewGroup parent = activity.findViewById(android.R.id.content);
            parent.removeView(root);
            ImitateDialogManager.popup(activity.getClass().getName(), this);
        }
    }

    public final boolean isShowing() {
        return root.getParent() != null
                && root.getVisibility() == View.VISIBLE;
    }
}