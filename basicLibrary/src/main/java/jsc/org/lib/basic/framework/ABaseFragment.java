package jsc.org.lib.basic.framework;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Stack;

import jsc.org.lib.basic.inter.OnFragmentEventListener;
import jsc.org.lib.basic.utils.ViewUtils;

public abstract class ABaseFragment extends Fragment {

    private View root;
    private boolean isFirstLoad = true;
    private boolean paused = true;

    private OnFragmentEventListener onFragmentEventListener = null;

    public abstract View initContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    public abstract void onLazyLoad();

    public void onReLazyLoad() {

    }

    /**
     * 回退按钮按下监听
     */
    @CallSuper
    public void onBackPressed() {

    }

    /**
     * 被移出FragmentManager
     */
    public void onRemoved() {

    }

    private final Stack<String> childFragmentStack = new Stack<>();

    public final boolean backChildFragment() {
        while (!childFragmentStack.isEmpty()) {
            String fragmentTag = childFragmentStack.pop();
            Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentTag);
            if (fragment == null) {
                continue;
            }

            if (fragment instanceof ABaseFragment) {
                ABaseFragment f = (ABaseFragment) fragment;
                if (!f.backChildFragment()) {
                    //remove self
                    f.onBackPressed();
                    removeChildFragment(f);
                    f.onRemoved();
                }
            } else {
                removeChildFragment(fragment);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (root == null) {
            root = initContentView(inflater, container);
            if (root == null) {
                TextView textView = new TextView(inflater.getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setText("请重写\"initContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container)\"方法。");
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                root = textView;
            } else if (root.getLayoutParams() == null) {
                root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        //防止点击穿透
        ViewUtils.disableCrossClick(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(getClass().getSimpleName(), "onConfigurationChanged: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (isFirstLoad) {
            isFirstLoad = false;
            onLazyLoad();
        } else {
            onReLazyLoad();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoad = true;
        root = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public View getRoot() {
        return root;
    }

    /**
     * Whether it comes into background.
     *
     * @return true, background; false, foreground
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Close soft input keyboard.
     */
    public final void hideInputMethod() {
        if (getContext() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && root != null) {
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
        }
    }

    public final void addChildFragment(@IdRes int containerViewId, Fragment childFragment, String tag) {
        addChildFragment(containerViewId, childFragment, tag, true);
    }

    public final void addChildFragment(@IdRes int containerViewId, Fragment childFragment, String tag, boolean intoChildFragmentStack) {
        if (childFragment != null) {
            getChildFragmentManager().beginTransaction().add(containerViewId, childFragment, tag).commit();
            if (intoChildFragmentStack) {
                pushIntoChildFragmentStack(tag);
            }
        }
    }

    public final void pushIntoChildFragmentStack(String tag) {
        childFragmentStack.push(tag);
    }

    public final void removeChildFragment(String tag) {
        removeChildFragment(getChildFragmentManager().findFragmentByTag(tag));
    }

    public final void removeChildFragment(Fragment childFragment) {
        if (childFragment != null) {
            getChildFragmentManager().beginTransaction().remove(childFragment).commit();
        }
    }

    public void setOnFragmentEventListener(OnFragmentEventListener onFragmentEventListener) {
        this.onFragmentEventListener = onFragmentEventListener;
    }

    public final void dispatchEvent(int key, Bundle data) {
        if (onFragmentEventListener != null) {
            onFragmentEventListener.onEvent(key, data);
        }
    }
}
