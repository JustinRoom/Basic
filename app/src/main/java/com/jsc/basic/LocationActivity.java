package com.jsc.basic;

import android.Manifest;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.util.Pair;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

import com.jsc.basic.databinding.ActivityLocationBinding;

import java.util.List;
import java.util.Locale;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.LocationLiveData;
import jsc.org.lib.basic.utils.ViewOutlineUtils;

public class LocationActivity extends ABaseActivity {

    ActivityLocationBinding binding = null;

    @Override
    public View initContentView() {
        registerPermissionLauncher();
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        binding.btnStartGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocationLiveData.getInstance().isStarted()) {
                    LocationLiveData.getInstance().setOnlyBeiDouLocation(false);
                    LocationLiveData.getInstance().requestGpsLocationUpdates();
                }
            }
        });
        binding.btnStartFused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocationLiveData.getInstance().isStarted()) {
                    LocationLiveData.getInstance().setOnlyBeiDouLocation(true);
                    LocationLiveData.getInstance().requestFusedLocationUpdates();
                }
            }
        });
        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationLiveData.getInstance().removeUpdates();
            }
        });
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnStartGps);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnStartFused);
        ViewOutlineUtils.applyHorizontalEllipticOutline(binding.btnStop);
        LocationLiveData.getInstance().observe(this, new Observer<Pair<Location, GnssStatus>>() {
            @Override
            public void onChanged(Pair<Location, GnssStatus> locationGnssStatusPair) {
                Location location = locationGnssStatusPair.first;
                GnssStatus status = locationGnssStatusPair.second;
                if (location != null) {
                    binding.tvLocation.setText(String.format(Locale.US, "经度:%s\n纬度:%s\n海拔:%s\n速度:%s m/s\n提供商:%s",
                            location.getLongitude(),
                            location.getLatitude(),
                            location.getAltitude(),
                            location.getSpeed(),
                            location.getProvider()));
                }
                if (status != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    //总数量
                    int count = status.getSatelliteCount();
                    int beidouInfixCount = 0;
                    //循环遍历卫星
                    for (int i = 0; i < count; i++) {
                        int id = status.getSvid(i);                         //卫星id
                        float DBHz = status.getCn0DbHz(i);                  //卫星信号强度
                        int type = status.getConstellationType(i);          //卫星星座类型
                        float azimuthDegrees = status.getAzimuthDegrees(i); //卫星方位角
                        boolean infix = status.usedInFix(i);                //卫星是否可用于定位
                        float elevationDegrees = status.getElevationDegrees(i);//卫星高程
                        boolean almanacData = status.hasAlmanacData(i);      //卫星是否具有年历数据
                        boolean ephemerisData = status.hasEphemerisData(i); //卫星是否具有星历数据
                        if (infix && type == GnssStatus.CONSTELLATION_BEIDOU) {
                            beidouInfixCount++;
                        }
                    }
                    binding.tvSatellite.setText(new StringBuilder()
                            .append("可定位卫星信号统计").append("\n")
                            .append("BEIDOU：").append(beidouInfixCount).append("\n"));
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onLazyLoad() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPermissionLaunchBack(String[] unGrantPermissions) {
        super.onPermissionLaunchBack(unGrantPermissions);
        if (unGrantPermissions.length == 0) {
            List<String> providers = LocationLiveData.getInstance().getAllProviders();
            StringBuilder builder = new StringBuilder();
            for (String p : providers) {
                builder.append(p).append("\u3000");
            }
            binding.tvTips.setText(builder.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationLiveData.getInstance().removeUpdates();
    }
}