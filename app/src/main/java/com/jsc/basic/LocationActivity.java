package com.jsc.basic;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jsc.basic.databinding.ActivityCircularProgressBinding;
import com.jsc.basic.databinding.ActivityLocationBinding;

import java.util.Locale;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.Locator;
import jsc.org.lib.basic.utils.ViewOutlineUtils;

public class LocationActivity extends ABaseActivity {

    ActivityLocationBinding binding = null;

    @Override
    public View initContentView() {
        registerPermissionLauncher();
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @Override
    public void onPermissionLaunchBack(String[] unGrantPermissions) {
        super.onPermissionLaunchBack(unGrantPermissions);
        if (unGrantPermissions.length == 0) {
            Locator.getInstance().requestGPSLocationUpdates(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    binding.tvLocation.setText(String.format(Locale.US, "lon:%s\nlat:%s", location.getLongitude(), location.getLatitude()));
                }
            });
        }
    }
}