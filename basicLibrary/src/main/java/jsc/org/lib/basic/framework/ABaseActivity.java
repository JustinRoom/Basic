package jsc.org.lib.basic.framework;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jsc.org.lib.basic.utils.ContentValuesUtils;
import jsc.org.lib.basic.widget.imitate.ImitateDialogManager;

public abstract class ABaseActivity extends AppCompatActivity {

    private boolean firstLoad = true;
    private ActivityResultLauncher<String[]> mPermissionLauncher = null;
    private final Map<String, ActivityResultLauncher<Intent>> mActivityResultLauncherMap = new HashMap<>();

    public boolean enableActionBar() {
        return true;
    }

    public boolean enableScreenshot() {
        return true;
    }

    public boolean enableKeepScreenOn() {
        return false;
    }

    public abstract View initContentView();

    public final int getWindowContentId() {
        return android.R.id.content;
    }

    public boolean onCustomBackPressed() {
        return false;
    }

    /**
     * Only call once.
     */
    public abstract void onLazyLoad();

    public void onReLazyLoad() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!enableActionBar()) {//无标题栏
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
        if (!enableScreenshot()) {//防截屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (enableKeepScreenOn()) {//防息屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        View view = initContentView();
        if (view != null) {
            setContentView(view);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firstLoad) {
            firstLoad = false;
            onLazyLoad();
        } else {
            onReLazyLoad();
        }
    }

    @Override
    public final void onBackPressed() {
        if (ImitateDialogManager.cancel(this)) return;
        if (backFragment()) return;
        if (onCustomBackPressed()) return;
        super.onBackPressed();
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>fragment manager start<<<<<<<<<<<<<<<<<<<<<<<
    private final Stack<String> fragmentStack = new Stack<>();

    /**
     * fragment回退
     *
     * @return true有fragment(或childFragment)
     */
    public final boolean backFragment() {
        while (!fragmentStack.isEmpty()) {
            String fragmentTag = fragmentStack.pop();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (fragment == null) {
                continue;
            }

            if (fragment instanceof ABaseFragment) {
                ABaseFragment f = (ABaseFragment) fragment;
                if (!f.backChildFragment()) {
                    //remove self
                    f.onBackPressed();
                    removeFragment(f);
                    f.onRemoved();
                }
            } else {
                removeFragment(fragment);
            }
            return true;
        }
        return false;
    }

    public final void addFragment(@IdRes int containerViewId, Fragment fragment, String tag) {
        addFragment(containerViewId, fragment, tag, true);
    }

    public final void addFragment(@IdRes int containerViewId, Fragment fragment, String tag, boolean intoFragmentStack) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(containerViewId, fragment, tag).commit();
            if (intoFragmentStack) {
                pushIntoFragmentStack(tag);
            }
        }
    }

    public final void pushIntoFragmentStack(String tag) {
        fragmentStack.push(tag);
    }

    public final void removeFragment(String tag) {
        removeFragment(getSupportFragmentManager().findFragmentByTag(tag));
    }

    public final void removeFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>fragment manager end<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstLoad", firstLoad);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCallbacksAndMessages();
        ImitateDialogManager.clear(this);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>Handler start<<<<<<<<<<<<<<<<<<<<<<<
    private Handler handler = null;

    public final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    handleMyMessage(msg);
                }
            };
        }
        return handler;
    }

    public void clearCallbacksAndMessages() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void handleMyMessage(@NonNull Message message) {

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>Handler end<<<<<<<<<<<<<<<<<<<<<<<

    public final void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public final void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view == null ? getWindow().getDecorView().getWindowToken() : view.getWindowToken(), 0);
        }
    }

    /**
     * Close soft input keyboard.
     */
    public final void hideInputMethod() {
        hideInputMethod(null);
    }

    public final void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, 0x8F));
        } else {
            vibrator.vibrate(200);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public final void vibrate(VibrationEffect effect) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(effect);
    }

    public final boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public final boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public final void showToast(@Nullable CharSequence txt) {
        if (!TextUtils.isEmpty(txt)) {
            Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }

    public final int getContentViewTop() {
        return getWindow().findViewById(getWindowContentId()).getTop();
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>> permissions
    public final void registerPermissionLauncher() {
        if (mPermissionLauncher == null) {
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
        }
    }

    public void onPermissionLaunchBack(String[] unGrantPermissions) {

    }

    /**
     * @param permissions the permissions to be requested
     * @see #registerPermissionLauncher()
     */
    public final void requestPermissions(String[] permissions) {
        //6.0版本以下不需要动态申请权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onPermissionLaunchBack(new String[]{});
            return;
        }
        List<String> list = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
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
            throw new IllegalStateException("Please call method 'registerPermissionLauncher()' first.");
        mPermissionLauncher.launch(unGrantPermissions);
    }

    public final void registerActivityResultLauncher(String key, ActivityResultCallback<ActivityResult> callback) {
        if (!mActivityResultLauncherMap.containsKey(key)) {
            ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), callback);
            mActivityResultLauncherMap.put(key, launcher);
        }
    }

    public final ActivityResultLauncher<Intent> getActivityResultLauncher(String key) {
        return mActivityResultLauncherMap.get(key);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>> External StorageManager
    public final void registerExternalStorageManagerLauncher() {
        registerActivityResultLauncher("externalStorageManager", new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                onExternalStorageManagerLaunchBack(result.getResultCode(), result.getData());
            }
        });
    }

    public void onExternalStorageManagerLaunchBack(int resultCode, @Nullable Intent data) {

    }

    /**
     * @param toSetting to system setting activity
     * @return true or false.
     * @see #registerExternalStorageManagerLauncher()
     */
    public final boolean isExternalStorageManager(boolean toSetting) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return true;
        //Android11及以上版本，申请sdcard读写权限
        boolean result = Environment.isExternalStorageManager();
        if (!result && toSetting) {
            ActivityResultLauncher<Intent> launcher = getActivityResultLauncher("externalStorageManager");
            if (launcher == null)
                throw new IllegalStateException("Please call method 'registerExternalStorageManagerLauncher()' first.");
            //manifest文件中需要申明"android.permission.MANAGE_EXTERNAL_STORAGE"权限
            launcher.launch(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
        }
        return result;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>> Draw Overlays
    public final void registerDrawOverlaysLauncher() {
        registerActivityResultLauncher("drawOverlays", new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                onDrawOverlaysLaunchBack(result.getResultCode(), result.getData());
            }
        });
    }

    public void onDrawOverlaysLaunchBack(int resultCode, @Nullable Intent data) {

    }

    /**
     * @param toSetting to system setting activity
     * @return true or false.
     * @see #registerDrawOverlaysLauncher()
     */
    public final boolean canDrawOverlays(boolean toSetting) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        //Android6及以上版本，申请悬浮窗权限
        boolean result = Settings.canDrawOverlays(this);
        if (!result && toSetting) {
            ActivityResultLauncher<Intent> launcher = getActivityResultLauncher("drawOverlays");
            if (launcher == null)
                throw new IllegalStateException("Please call method 'registerDrawOverlaysLauncher()' first.");
            //manifest文件中需要申明"android.permission.SYSTEM_ALERT_WINDOW"权限
            launcher.launch(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        }
        return result;
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>> Take Photo
    public final void registerTakePhotoLauncher() {
        registerActivityResultLauncher("takePhoto", new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                String path = null;
                if (result.getResultCode() == RESULT_OK) {
                    path = ContentValuesUtils.queryPath(getApplicationContext(), mPhotoUri);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    getContentResolver().delete(mPhotoUri, null);
                } else {
                    //do nothing
                }
                onTakePhotoLaunchBack(result.getResultCode(), path);
            }
        });
    }

    public void onTakePhotoLaunchBack(int resultCode, String path) {

    }

    private Uri mPhotoUri = null;

    /**
     * @see #registerTakePhotoLauncher()
     */
    public final void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ActivityResultLauncher<Intent> launcher = getActivityResultLauncher("takePhoto");
        if (launcher == null)
            throw new IllegalStateException("Please call method 'registerTakePhotoLauncher()' first.");
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "pic_" + System.currentTimeMillis() + ".JPEG");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        mPhotoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        launcher.launch(intent);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>> Pick Image
    public final void registerPickImageLauncher() {
        registerActivityResultLauncher("pickImage", new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                String path = "";
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    Uri uri = intent == null ? null : intent.getData();
                    if (uri != null) {
                        path = ContentValuesUtils.queryPath(getApplicationContext(), uri);
                    }
                }
                onPickImageLaunchBack(result.getResultCode(), path);
            }
        });
    }

    public void onPickImageLaunchBack(int resultCode, String path) {

    }

    public final void pickImage() {
        ActivityResultLauncher<Intent> launcher = getActivityResultLauncher("pickImage");
        if (launcher == null)
            throw new IllegalStateException("Please call method 'registerPickImageLauncher()' first.");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }
}
