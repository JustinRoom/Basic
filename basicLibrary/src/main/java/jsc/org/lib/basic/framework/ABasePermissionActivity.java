package jsc.org.lib.basic.framework;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class ABasePermissionActivity extends ABaseActivity {

    private final List<String> permissionArray = new ArrayList<>();

    public void initPermissions(@NonNull List<String> permissions) {

    }

    public abstract void onAllPermissionsChecked();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initPermissions(permissionArray);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        requestPermissions();
    }

    private void requestPermissions() {
        //6.0版本以下不需要动态申请权限
        if (Build.VERSION.SDK_INT < 23) {
            onAllPermissionsChecked();
            return;
        }

        List<String> result = hasPermissions(permissionArray);
        if (result == null || result.isEmpty()) {
            onAllPermissionsChecked();
        } else {
            String[] perms = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                perms[i] = result.get(i);
            }
            ActivityCompat.requestPermissions(this, perms, 0x10);
        }
    }

    private List<String> hasPermissions(@NonNull List<String> perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // DANGER ZONE!!! Changing this will break the library.
            return null;
        }
        List<String> unGrantedList = new ArrayList<>();
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                unGrantedList.add(perm);
            }
        }
        return unGrantedList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        if (isAllGranted) {
            onAllPermissionsChecked();
        }
    }
}
