package com.jsc.basic;

import android.Manifest;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

import com.jsc.basic.databinding.ActivityLocationBinding;

import java.util.List;
import java.util.Locale;

import jsc.org.lib.basic.framework.ABaseActivity;
import jsc.org.lib.basic.object.LocationLiveData;

public class LocationActivity extends ABaseActivity {

    ActivityLocationBinding binding = null;

    @Override
    public View initContentView() {
        registerPermissionLauncher();
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        LocationLiveData.getInstance().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                binding.tvLocation.setText(String.format(Locale.US, "经度:%s\n纬度:%s\n海拔:%s\n速度:%s m/s\n提供商:%s",
                        location.getLongitude(),
                        location.getLatitude(),
                        location.getAltitude(),
                        location.getSpeed(),
                        location.getProvider()));
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
            LocationLiveData.getInstance().requestLocationUpdates();
            LocationLiveData.getInstance().setGnssStatusCallback(new LocationLiveData.GnssStatusCallback() {
                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    //总数量
                    int count = status.getSatelliteCount();
                    int gpsInfixCount = 0;
                    int sbasInfixCount = 0;
                    int glonassInfixCount = 0;
                    int qzssInfixCount = 0;
                    int beidouInfixCount = 0;
                    int galileoInfixCount = 0;
                    int irnssInfixCount = 0;
                    int beidouNotInfixCount = 0;
                    //如果卫星总数大于0
                    if (count > 0) {
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
                            if (infix) {
                                switch (type) {
                                    case GnssStatus.CONSTELLATION_GPS:
                                        gpsInfixCount++;
                                        break;
                                    case GnssStatus.CONSTELLATION_SBAS:
                                        sbasInfixCount++;
                                        break;
                                    case GnssStatus.CONSTELLATION_GLONASS:
                                        glonassInfixCount++;
                                        break;
                                    case GnssStatus.CONSTELLATION_QZSS:
                                        qzssInfixCount++;
                                        break;
                                    case GnssStatus.CONSTELLATION_BEIDOU:
                                        beidouInfixCount++;
                                        break;
                                    case GnssStatus.CONSTELLATION_GALILEO:
                                        galileoInfixCount++;
                                        break;
                                    case GnssStatus.CONSTELLATION_IRNSS:
                                        irnssInfixCount++;
                                        break;
                                }
                            } else {
                                if (type == GnssStatus.CONSTELLATION_BEIDOU) {
                                    beidouNotInfixCount++;
                                }
                            }
                        }
                    }
                    binding.tvSatellite.setText(new StringBuilder()
                            .append("可定位卫星信号统计").append("\n")
                            .append("GPS：").append(gpsInfixCount).append("\n")
                            .append("SBAS：").append(sbasInfixCount).append("\n")
                            .append("GLONASS：").append(glonassInfixCount).append("\n")
                            .append("QZSS：").append(qzssInfixCount).append("\n")
                            .append("BEIDOU：").append(beidouInfixCount).append("\n")
                            .append("GALILEO：").append(galileoInfixCount).append("\n")
                            .append("IRNSS：").append(irnssInfixCount).append("\n")
                            .append("不可定位卫星信号统计").append("\n")
                            .append("BEIDOU：").append(beidouNotInfixCount).append("\n")
                    );
                    if (beidouInfixCount > 0) {
                        Toast.makeText(LocationActivity.this, String.format(Locale.US, "接收到%d个北斗信号", beidouInfixCount), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationLiveData.getInstance().setGnssStatusCallback(null);
    }
}