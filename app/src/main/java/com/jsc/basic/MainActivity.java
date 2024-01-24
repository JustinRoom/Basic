package com.jsc.basic;

import android.Manifest;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jsc.basic.databinding.ActivityMainBinding;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.LoggerImpl;
import jsc.org.lib.basic.utils.ImagePreviewDialogUtils;
import jsc.org.lib.basic.utils.ItemBackgroundUtils;
import jsc.org.lib.basic.utils.ViewOutlineUtils;
import jsc.org.lib.basic.utils.ViewUtils;
import jsc.org.lib.basic.widget.CustomTitleBar;
import jsc.org.lib.basic.widget.imitate.ImitateLoadingDialog;
import jsc.org.lib.basic.widget.imitate.ImitateToast;

public class MainActivity extends ABaseActivity {

    ActivityMainBinding binding = null;
    int index = 0;

    @Override
    public boolean enableActionBar() {
        return false;
    }

    @Override
    public View initContentView() {
        registerPermissionLauncher();
        registerExternalStorageManagerLauncher();
        registerDrawOverlaysLauncher();
        registerTakePhotoLauncher();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.titleBar.setTitle("主页");
        ItemBackgroundUtils.applyItemBackgroundBorderlessRipple(binding.titleBar.findChildByKey(CustomTitleBar.BACK_CONTAINER));
        binding.titleBar.findChildByKey(CustomTitleBar.BACK_CONTAINER).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.btnImitateLoadingDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImitateToast.show("Loading...", Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, getContentViewTop() + ViewUtils.statusBarHeight(getResources()) + 12);
                showLoadingDialog();
            }
        });
        binding.btnCircularProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), CircularProgressActivity.class));
            }
        });
        binding.btnLogger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                ImitateToast.show("clicked button" + index);
                LoggerImpl.getInstance().i("ViewClick", index + " clicked the view.", true);
            }
        });
        binding.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        binding.btnRoundCornerViewOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RoundCornerViewOutlineActivity.class));
            }
        });
        binding.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), LocationActivity.class));
            }
        });
        binding.btnSubFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubFragment();
            }
        });
        binding.btnSubViewModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubViewModule();
            }
        });
        binding.btnExif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ExifInterfaceActivity.class));
            }
        });
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnImitateLoadingDialog);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnCircularProgress);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnLogger);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnTakePhoto);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnRoundCornerViewOutline);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnLocation);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnSubFragment);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnSubViewModule);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnExif);
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public boolean onCustomBackPressed() {
        if (viewModule.isShowing()) {
            viewModule.hide();
            return true;
        }
        return super.onCustomBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImitateToast.unInit();
    }

    private void showLoadingDialog() {
        ImitateLoadingDialog dialog = new ImitateLoadingDialog(this);
        dialog.setCancelable(true);
        dialog.setCancelableTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onPermissionLaunchBack(String[] unGrantPermissions) {
        super.onPermissionLaunchBack(unGrantPermissions);
        if (unGrantPermissions.length == 0) {
            showToast("所有权限已申请允许。");
            if (isExternalStorageManager(true)) {
                canDrawOverlays(true);
            }
        }
    }

    @Override
    public void onExternalStorageManagerLaunchBack(int resultCode, @Nullable Intent data) {
        super.onExternalStorageManagerLaunchBack(resultCode, data);
        if (isExternalStorageManager(false)) {
            canDrawOverlays(true);
        }
    }

    @Override
    public void onTakePhotoLaunchBack(int resultCode, String path) {
        super.onTakePhotoLaunchBack(resultCode, path);
        if (!TextUtils.isEmpty(path)) {
            ImagePreviewDialogUtils.showImagePreviewDialog(this, path);
        }
    }

    private void showSubFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("_subPage");
        if (fragment == null) {
            fragment = new SubPageFragment();
            addFragment(R.id.fy_sub_page_container, fragment, "_subPage");
        }
    }

    private final SubViewModule viewModule = new SubViewModule();
    private void showSubViewModule() {
        if (!viewModule.isInitialized()) {
            viewModule.init(this, binding.fySubPageContainer, null);
        }
        if (!viewModule.isShowing()) {
            viewModule.show();
        }
        viewModule.runShowAnimation();
    }
}