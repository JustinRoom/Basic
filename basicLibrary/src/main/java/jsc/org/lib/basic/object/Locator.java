package jsc.org.lib.basic.object;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

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

    /**
     * 经度：{@link Location#getLongitude()}<br>
     * 纬度：{@link Location#getLatitude()}<br>
     * 海拔：{@link Location#getAltitude()}<br>
     * 速度：{@link Location#getSpeed()}<br>
     *
     * @param context
     * @param minTimeMs
     * @param minDistanceM
     * @param listener
     */
    public void requestLocationUpdates(@NonNull Context context,
                                       long minTimeMs,
                                       float minDistanceM,
                                       @NonNull LocationListener listener,
                                       @Nullable GnssStatus.Callback callback) {
        if (mLocationManager == null) return;
        Criteria crt = new Criteria(); //位置监听标准
        crt.setHorizontalAccuracy(Criteria.ACCURACY_HIGH); //水平精度高
        crt.setAltitudeRequired(true);//需要高度
        String provider = mLocationManager.getBestProvider(crt, false);//寻找最匹配的provider
        //添加卫星状态改变监听
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //判断GPS是否正常启动
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mLocationManager.registerGnssStatusCallback(callback, null);
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMs, minDistanceM, listener);
            }
        }
    }
}
