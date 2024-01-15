package jsc.org.lib.basic.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("MissingPermission")
public class LocationLiveData extends LiveData<Location> {

    private LocationManager mLocationManager;
    private GnssStatusCallback gnssStatusCallback = null;
    private long minTimeMs;
    private float minDistanceM;
    private boolean started = false;
    private boolean active = false;

    public interface GnssStatusCallback {
        void onSatelliteStatusChanged(@NonNull GnssStatus status);
    }

    private static class SingletonHolder {
        private static final LocationLiveData INSTANCE = new LocationLiveData();
    }

    private LocationLiveData() {

    }

    public static LocationLiveData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context c) {
        init(c, 5000L, 1);
    }

    public void init(Context c, long minTimeMs, float minDistanceM) {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) c.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            this.minTimeMs = minTimeMs;
            this.minDistanceM = minDistanceM;
        }
    }

    /**
     * 经度：{@link Location#getLongitude()}<br>
     * 纬度：{@link Location#getLatitude()}<br>
     * 海拔：{@link Location#getAltitude()}<br>
     * 速度：{@link Location#getSpeed()}<br>
     */
    public void requestLocationUpdates() {
        if (mLocationManager == null) return;
        //判断GPS是否正常启动
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !started) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mLocationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                    @Override
                    public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                        super.onSatelliteStatusChanged(status);
                        if (active && gnssStatusCallback != null) {
                            gnssStatusCallback.onSatelliteStatusChanged(status);
                        }
                    }
                });
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMs, minDistanceM, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (active) {
                        setValue(location);
                    }
                }
            });
            started = true;
        }
    }

    public List<String> getAllProviders() {
        return mLocationManager == null ? new ArrayList<>() : mLocationManager.getAllProviders();
    }

    public void setGnssStatusCallback(GnssStatusCallback gnssStatusCallback) {
        this.gnssStatusCallback = gnssStatusCallback;
    }

    @Override
    protected void onActive() {
        active = true;
    }

    @Override
    protected void onInactive() {
        active = false;
    }
}