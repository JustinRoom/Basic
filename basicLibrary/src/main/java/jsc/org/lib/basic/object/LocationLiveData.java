package jsc.org.lib.basic.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * 转百度地图坐标：<br>
 * <code>
 * Location latLng;<br>
 * CoordinateConverter converter  = new CoordinateConverter();<br>
 * converter.from(CoordType.GPS);<br>
 * converter.coord(latLng);<br>
 * LatLng desLatLng = converter.convert();
 * </code>
 * <br>
 * <br>
 * 转高德地图坐标：<br>
 * <code>
 * Location latLng;<br>
 * GeoPoint point = CoordinateConvert.fromGpsToAMap(latLng.getLatitude(), latLng.getLongitude());<br>
 * LatLonPoint latLonPoint = new LatLonPoint(point.getLatitudeE6()*1.E-6, point.getLongitudeE6()*1.E-6);
 * </code>
 */
@SuppressLint("MissingPermission")
public class LocationLiveData extends LiveData<Pair<Location, GnssStatus>> {

    private LocationManager mLocationManager;
    private long minTimeMs;
    private float minDistanceM;
    private boolean started = false;
    private boolean active = false;
    private boolean onlyBeiDouLocation = false;
    private int count = 0;
    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (active) {
                if (isOnlyBeiDouLocation()) {
                    if (fromBeiDou(location)) {
                        setValue(new Pair<>(location, null));
                    }
                    return;
                }
                setValue(new Pair<>(location, null));
            }
        }
    };

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

    public boolean isOnlyBeiDouLocation() {
        return onlyBeiDouLocation;
    }

    public void setOnlyBeiDouLocation(boolean onlyBeiDouLocation) {
        this.onlyBeiDouLocation = onlyBeiDouLocation;
    }

    public void requestFusedLocationUpdates() {
        //LocationManager.FUSED_PROVIDER = "fused"
        requestLocationUpdates("fused");
    }

    public void requestGpsLocationUpdates() {
        requestLocationUpdates(LocationManager.GPS_PROVIDER);
    }

    /**
     * 经度：{@link Location#getLongitude()}<br>
     * 纬度：{@link Location#getLatitude()}<br>
     * 海拔：{@link Location#getAltitude()}<br>
     * 速度：{@link Location#getSpeed()}<br>
     */
    private void requestLocationUpdates(@NonNull String provider) {
        if (mLocationManager == null) return;
        //判断GPS是否正常启动
        if (started) return;
        started = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    if (active) {
                        count++;
                        if (count == 5) {
                            setValue(new Pair<>(null, status));
                            count = 0;
                        }
                    }
                }
            });
        }
        //public static final String FUSED_PROVIDER = "fused";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && LocationManager.FUSED_PROVIDER.equals(provider)) {
            Criteria criteria = new Criteria();
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            mLocationManager.requestLocationUpdates(minTimeMs, minDistanceM, criteria, listener, Looper.getMainLooper());
            return;
        }
        mLocationManager.requestLocationUpdates(provider, minTimeMs, minDistanceM, listener);
    }

    public void removeUpdates() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(listener);
        }
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public List<String> getAllProviders() {
        return mLocationManager == null ? new ArrayList<>() : mLocationManager.getAllProviders();
    }

    public static boolean fromBeiDou(@NonNull Location location) {
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        int latIndexOf = lat.indexOf(".");
        int lngIndexOf = lng.indexOf(".");
        return lat.substring(latIndexOf + 1).length() >= 14
                && lng.substring(lngIndexOf + 1).length() >= 14;

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