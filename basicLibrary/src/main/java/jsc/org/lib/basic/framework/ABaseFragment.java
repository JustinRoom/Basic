package jsc.org.lib.basic.framework;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jsc.org.lib.basic.inter.OnFragmentEventListener;
import jsc.org.lib.basic.utils.ViewUtils;

public abstract class ABaseFragment extends Fragment {

    private View root;
    private boolean isFirstLoad = true;
    private boolean paused = true;
    private ActivityResultLauncher<String[]> mPermissionLauncher = null;
    private ActivityResultLauncher<Intent> mExternalStorageManagerLauncher = null;

    public boolean registerPermissionLauncher() {
        return false;
    }

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
        if (registerPermissionLauncher()) {
            mPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    List<String> list = new ArrayList<>();
                    for (String permission : result.keySet()) {
                        if (Boolean.FALSE.equals(result.get(permission))) {
                            list.add(permission);
                        }
                    }
                    String[] unGrantPermissions = new String[list.size()];
                    list.toArray(unGrantPermissions);
                    onPermissionLaunchBack(unGrantPermissions);
                }
            });
            mExternalStorageManagerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    onExternalStorageManagerLaunchBack(result.getResultCode(), result.getData());
                }
            });
        }
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
                textView.setText("请重写\"getContentLayoutId()\"或\"createContentView(Context)\"方法。");
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

    //>>>>>>>>>>>>>>>>>>>>>>>>>>> about permissions
    public final void requestPermissions(String[] permissions) {
        //6.0版本以下不需要动态申请权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onPermissionLaunchBack(new String[]{});
            return;
        }
        List<String> list = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                list.add(perm);
            }
        }
        if (list.isEmpty()) {
            onPermissionLaunchBack(new String[]{});
            return;
        }
        String[] unGrantPermissions = new String[list.size()];
        list.toArray(unGrantPermissions);
        if (mPermissionLauncher == null)
            throw new IllegalStateException("Please override method 'registerPermissionLauncher()' for true result.");
        mPermissionLauncher.launch(unGrantPermissions);
    }

    public void onPermissionLaunchBack(String[] unGrantPermissions) {

    }

    public final boolean isExternalStorageManager(boolean toSetting) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return true;
        //Android11及以上版本，申请sdcard读写权限
        boolean result = Environment.isExternalStorageManager();
        if (!result && toSetting) {
            if (mExternalStorageManagerLauncher == null)
                throw new IllegalStateException("Please override method 'registerPermissionLauncher()' for true result.");
            //manifest文件中需要申明"android.permission.MANAGE_EXTERNAL_STORAGE"权限
            mExternalStorageManagerLauncher.launch(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
        }
        return result;
    }

    public void onExternalStorageManagerLaunchBack(int resultCode, @Nullable Intent data) {

    }
}
