package jsc.org.lib.basic.widget.dialog;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Locale;

import jsc.org.lib.basic.R;
import jsc.org.lib.basic.databinding.DialogCustomExpandabelBinding;

public final class CustomExpandableDialog extends Dialog {
    private FrameLayout root = null;
    private TextView tipsView = null;
    private DialogCustomExpandabelBinding mBinding = null;
    private int windowGravity = Gravity.CENTER;
    private int windowAnimation = -1;
    private Point landscapeSize = null;
    private Point portraitSize = null;
    private boolean tipsStyle = false;
    private boolean initialized = false;
    private boolean autoDismissAfterClickAction = true;
    private OnActionListener callback = null;
    private Bundle arguments = null;

    public interface OnActionListener {

        void onClose(View view, Bundle data);

        void onNegative(View view, Bundle data);

        void onPositive(View view, Bundle data);

        /**
         * Life cycle : called before {@link #onDismiss(View, Bundle)}.
         *
         * @param view root view.
         * @param data arguments
         */
        void onCancel(View view, Bundle data);

        /**
         * Life cycle : called after {@link #onCancel(View, Bundle)}.
         *
         * @param view root view.
         * @param data arguments
         */
        void onDismiss(View view, Bundle data);
    }

    public static class SimpleOnActionListener implements OnActionListener {

        @Override
        public void onClose(View view, Bundle data) {

        }

        @Override
        public void onNegative(View view, Bundle data) {

        }

        @Override
        public void onPositive(View view, Bundle data) {

        }

        @Override
        public void onCancel(View view, Bundle data) {

        }

        @Override
        public void onDismiss(View view, Bundle data) {

        }
    }

    public CustomExpandableDialog(@NonNull Context context) {
        this(context, R.style.CustomExpandableDialogStyle);
    }

    private CustomExpandableDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private boolean isLandscape() {
        return getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = windowGravity;
        Point size = isLandscape() ? landscapeSize : portraitSize;
        if (size == null) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            if (isLandscape()) {
                size = new Point(displayMetrics.widthPixels / 2, displayMetrics.heightPixels * 2 / 5);
            } else {
                size = new Point(displayMetrics.widthPixels * 4 / 5, displayMetrics.heightPixels * 2 / 5);
            }
        }
        if (windowAnimation != -1) {
            params.windowAnimations = windowAnimation;
        }
        params.width = size.x;
        params.height = size.y;
        window.setAttributes(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new FrameLayout(getContext());
        root.setBackgroundColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            root.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, view.getContext().getResources().getDisplayMetrics());
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            });
            root.setClipToOutline(true);
        }
        setContentView(root, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mBinding = DialogCustomExpandabelBinding.inflate(getLayoutInflater(), root, true);
        if (tipsStyle) {
            ScrollView scrollView = new ScrollView(root.getContext());
            //setFillViewport
            //当子布局高度小于ScrollView的高度时，定义子布局match_parent或者fill_parent不起作用，因此设置layout_gravity也不起作用。
            // 在scrollview里添加属性android:fillViewport=”true” 就可以了，使得子布局高度和scrollview一样，而当子布局高度超过scrollview的高度时，这个属性就没有意义了。
            scrollView.setFillViewport(true);
            mBinding.fyContentContainer.addView(scrollView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            tipsView = new TextView(root.getContext());
            tipsView.setTextColor(0xFF333333);
            scrollView.addView(tipsView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        mBinding.ivClose.setColorFilter(Color.RED);
        mBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCountDownAnim();
                if (autoDismissAfterClickAction) {
                    dismiss();
                }
                if (callback != null) {
                    callback.onClose(v, arguments);
                }
            }
        });
        mBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCountDownAnim();
                if (autoDismissAfterClickAction) {
                    dismiss();
                }
                if (callback != null) {
                    callback.onNegative(v, arguments);
                }
            }
        });
        mBinding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCountDownAnim();
                if (autoDismissAfterClickAction) {
                    dismiss();
                }
                if (callback != null) {
                    callback.onPositive(v, arguments);
                }
            }
        });
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelCountDownAnim();
                if (callback != null) {
                    callback.onCancel(root, arguments);
                }
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (callback != null) {
                    callback.onDismiss(root, arguments);
                }
            }
        });
    }

    /**
     * Use {@link #showMyself()} instead of.
     */
    @Deprecated
    @Override
    public final void show() {
        if (!initialized) {
            initialized = true;
            onInitialize();
        }
        super.show();
    }

    public void onInitialize() {

    }

    public CustomExpandableDialog windowGravity(int windowGravity) {
        this.windowGravity = windowGravity;
        return this;
    }

    public CustomExpandableDialog windowAnimation(@StyleRes int windowAnimation) {
        this.windowAnimation = windowAnimation;
        return this;
    }

    public CustomExpandableDialog landscapeSize(float widthPercent, float heightPercent) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * widthPercent);
        int height = (int) (metrics.heightPixels * heightPercent);
        return landscapeSize(width, height);
    }

    public CustomExpandableDialog landscapeSize(int width, float heightPercent) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels * heightPercent);
        return landscapeSize(width, height);
    }

    public CustomExpandableDialog landscapeSize(float widthPercent, int height) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * widthPercent);
        return landscapeSize(width, height);
    }

    public CustomExpandableDialog landscapeSize(int width, int height) {
        landscapeSize = new Point(width / 2 * 2, height / 2 * 2);
        return this;
    }

    public CustomExpandableDialog portraitSize(float widthPercent, float heightPercent) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * widthPercent);
        int height = (int) (metrics.heightPixels * heightPercent);
        return portraitSize(width, height);
    }

    public CustomExpandableDialog portraitSize(int width, float heightPercent) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels * heightPercent);
        return portraitSize(width, height);
    }

    public CustomExpandableDialog portraitSize(float widthPercent, int height) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * widthPercent);
        return portraitSize(width, height);
    }

    public CustomExpandableDialog portraitSize(int width, int height) {
        portraitSize = new Point(width / 2 * 2, height / 2 * 2);
        return this;
    }

    public CustomExpandableDialog arguments(Bundle arguments) {
        this.arguments = arguments;
        return this;
    }

    /**
     * Called before {@link #showMyself()}.
     *
     * @return dialog instance
     */
    public CustomExpandableDialog asTipsStyle() {
        this.tipsStyle = true;
        return this;
    }

    public CustomExpandableDialog setActionCallback(OnActionListener callback) {
        this.callback = callback;
        return this;
    }

    public CustomExpandableDialog cancelable(boolean cancelable) {
        setCancelable(cancelable);
        return this;
    }

    public CustomExpandableDialog canceledOnTouchOutside(boolean canceledOnTouchOutside) {
        setCanceledOnTouchOutside(canceledOnTouchOutside);
        return this;
    }

    public CustomExpandableDialog notAutoDismissAfterClickAction() {
        this.autoDismissAfterClickAction = false;
        return this;
    }

    public CustomExpandableDialog showMyself() {
        show();
        return this;
    }

    public CustomExpandableDialog title(boolean show, CharSequence title) {
        return title(show, Gravity.CENTER_HORIZONTAL, title);
    }

    public CustomExpandableDialog title(boolean show, int gravity, CharSequence title) {
        checkShowState();
        mBinding.tvTitle.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.tvTitle.setGravity(gravity);
        mBinding.tvTitle.setText(title);
        return this;
    }

    public CustomExpandableDialog subTitle(boolean show, CharSequence subTitle) {
        return subTitle(show, Gravity.CENTER_HORIZONTAL, subTitle);
    }

    public CustomExpandableDialog subTitle(boolean show, int gravity, CharSequence subTitle) {
        checkShowState();
        mBinding.tvSubTitle.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.tvSubTitle.setGravity(gravity);
        mBinding.tvSubTitle.setText(subTitle);
        return this;
    }

    public CustomExpandableDialog negativeButton(CharSequence txt) {
        checkShowState();
        mBinding.btnCancel.setText(txt);
        return this;
    }

    public CustomExpandableDialog positiveButton(CharSequence txt) {
        checkShowState();
        mBinding.btnOk.setText(txt);
        return this;
    }

    public CustomExpandableDialog buttonVisibility(boolean showNegative, boolean showPositive) {
        checkShowState();
        mBinding.btnCancel.setVisibility(showNegative ? View.VISIBLE : View.GONE);
        mBinding.btnOk.setVisibility(showPositive ? View.VISIBLE : View.GONE);
        if (showNegative && showPositive) {
            //do nothing
        } else if (!showNegative && !showPositive) {
            //do nothing
        } else {
            ConstraintLayout.LayoutParams params;
            if (showNegative) {
                params = (ConstraintLayout.LayoutParams) mBinding.btnCancel.getLayoutParams();
                params.rightToRight = ConstraintSet.PARENT_ID;
            } else {
                params = (ConstraintLayout.LayoutParams) mBinding.btnOk.getLayoutParams();
                params.leftToLeft = ConstraintSet.PARENT_ID;
            }
            params.width = getWindow().getAttributes().width / 2;
        }
        return this;
    }

    public CustomExpandableDialog tips(CharSequence tips) {
        return tips(Gravity.CENTER, tips);
    }

    public CustomExpandableDialog tips(int gravity, CharSequence tips) {
        checkShowState();
        if (tipsStyle && tipsView != null) {
            tipsView.setGravity(gravity);
            tipsView.setText(tips);
        }
        return this;
    }

    @Nullable
    public FrameLayout getRoot() {
        return root;
    }

    @Nullable
    public TextView getTipsView() {
        return tipsView;
    }

    public DialogCustomExpandabelBinding getViewBinding() {
        checkShowState();
        return mBinding;
    }

    public final void checkShowState() {
        if (!initialized)
            throw new IllegalStateException("Please call showMyself() first.");
    }

    private ValueAnimator animator = null;
    private TextView tempButton = null;
    private int key = 0;

    public void countDown(String target, int seconds) {
        if ("cancel".equalsIgnoreCase(target)) {
            tempButton = mBinding.btnCancel;
            key = 0;
        } else if ("ok".equalsIgnoreCase(target)) {
            tempButton = mBinding.btnOk;
            key = 1;
        } else {
            tempButton = null;
        }
        if (tempButton == null) {
            return;
        }

        final String txt = tempButton.getText().toString() + "(%d秒)";
        if (animator == null) {
            animator = ValueAnimator.ofInt(seconds, 0);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (Integer) animation.getAnimatedValue();
                    if (value == 0) {
                        dismiss();
                        if (callback == null) return;
                        if (key == 0) {
                            callback.onNegative(tempButton, null);
                        } else {
                            callback.onPositive(tempButton, null);
                        }
                    } else {
                        tempButton.setText(String.format(Locale.US, txt, value - 1));
                    }
                }
            });
            animator.setDuration(seconds * 1000L);
        }
        tempButton.setText(String.format(Locale.US, txt, seconds));
        animator.start();
    }

    public void cancelCountDownAnim() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }
}