package com.jsc.basic;

import android.Manifest;
import android.view.Gravity;
import android.view.View;

import com.jsc.basic.databinding.ActivityMainBinding;

import jsc.org.lib.basic.framework.ABaseActivity;
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
        ImitateToast.init(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
            }
        });
        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToast("clicked button");
                index++;
                ImitateToast.show("clicked button" + index);
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
            isExternalStorageManager(true);
        }
    }
}