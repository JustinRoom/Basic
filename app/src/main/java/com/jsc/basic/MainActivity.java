package com.jsc.basic;

import android.Manifest;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;

import com.jsc.basic.databinding.ActivityMainBinding;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.LoggerImpl;
import jsc.org.lib.basic.utils.ItemBackgroundUtils;
import jsc.org.lib.basic.widget.CustomTitleBar;
import jsc.org.lib.basic.widget.imitate.ImitateLoadingDialog;
import jsc.org.lib.basic.widget.imitate.ImitateToast;

public class MainActivity extends ABaseActivity {

    ActivityMainBinding binding = null;
    int index = 0;

    @Override
    public boolean registerPermissionLauncher() {
        return true;
    }

    @Override
    public View initContentView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.titleBar.setTitle("主页");
        ItemBackgroundUtils.applyItemBackgroundBorderlessRipple(binding.titleBar.findChildByKey(CustomTitleBar.BACK_CONTAINER));
        binding.titleBar.findChildByKey(CustomTitleBar.BACK_CONTAINER).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
            }
        });
        binding.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SubActivity.class));
            }
        });
        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                ImitateToast.show("clicked button" + index);
                LoggerImpl.getInstance().i("ViewClick", index + " clicked the view.", true);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
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
}