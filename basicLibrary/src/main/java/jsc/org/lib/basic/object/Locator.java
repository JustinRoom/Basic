package jsc.org.lib.basic.object;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public final class Locator {

    private LocationManager mLocationManager = null;

    private static class SingletonHolder {
        private static final Locator INSTANCE = new Locator();
    }

    private Locator() {
    }

    public static Locator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始化定位管理,android自带卫星
     */
    public void init(Context c) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) c.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public LocationManager getLocationManager() {
        return mLocationManager;
    }

    public void requestGPSLocationUpdates(@NonNull Context context, @NonNull LocationListener listener) {
        requestGPSLocationUpdates(context, 1000, 1, listener);
    }

    public void requestGPSLocationUpdates(@NonNull Context context, long minTimeMs, float minDistanceM, @NonNull LocationListener listener) {
        requestLocationUpdates(context, LocationManager.GPS_PROVIDER, minTimeMs, minDistanceM, listener);
    }

    /**
     * 经度：{@link Location#getLongitude()}<br>
     * 纬度：{@link Location#getLatitude()}
     * @param context
     * @param provider
     * @param minTimeMs
     * @param minDistanceM
     * @param listener
     */
    public void requestLocationUpdates(@NonNull Context context, String provider, long minTimeMs, float minDistanceM, @NonNull LocationListener listener) {
        if (mLocationManager == null) return;
        //添加卫星状态改变监听
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //判断GPS是否正常启动
            if (mLocationManager.isProviderEnabled(provider)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mLocationManager.registerGnssStatusCallback(new LocateCallback(), null);
                }
                mLocationManager.requestLocationUpdates(provider, minTimeMs, minDistanceM, listener);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public class LocateCallback extends GnssStatus.Callback {
        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            super.onSatelliteStatusChanged(status);
            //解析组装卫星信息
            makeGnssStatus(status);
        }

        @Override
        public void onStarted() {
            super.onStarted();
        }

        @Override
        public void onStopped() {
            super.onStopped();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void makeGnssStatus(GnssStatus status) {
        //当前可以获取到的卫星总数，然后遍历
        int satelliteCount = status.getSatelliteCount();
        if (satelliteCount > 0) {
            for (int i = 0; i < satelliteCount; i++) {
                //GnssStatus的大部分方法参数传入的就是卫星数量的角标
                //获取卫星类型
                int type = status.getConstellationType(i);
                if (GnssStatus.CONSTELLATION_BEIDOU == type) {
                    //北斗卫星类型的判断
                }
            }
        }
    }
}
