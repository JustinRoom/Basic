package com.jsc.basic;

import android.Manifest;
import android.view.View;
import android.widget.Toast;

import com.jsc.basic.databinding.ActivityMainBinding;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.widget.imitate.ImitateLoadingDialog;

public class MainActivity extends ABaseActivity {

    ActivityMainBinding binding = null;

    @Override
    public boolean registerPermissionLauncher() {
        return true;
    }

    @Override
    public View initContentView() {
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
                Toast.makeText(v.getContext(), "clicked button", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "所有权限已申请允许。", Toast.LENGTH_SHORT).show();
            isExternalStorageManager(true);
        }
    }
}