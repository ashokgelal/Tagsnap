package com.ashokgelal.tagsnap.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.ashokgelal.tagsnap.listeners.LocationResultListener;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService {
    private final LocationListener mGpsLocationListener;
    private final LocationListener mNetworkLocationListener;
    private LocationResultListener mLocationResultListener;
    private LocationManager mLocationManager;
    private Timer mTimer;
    private boolean mGpsEnabled;
    private boolean mNetworkEnabled;

    public LocationService() {
        mGpsLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mTimer.cancel();
                mLocationManager.removeUpdates(this);
                mLocationManager.removeUpdates(mNetworkLocationListener);
                mLocationResultListener.onLocationResultAvailable(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        mNetworkLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mTimer.cancel();
                mLocationManager.removeUpdates(this);
                mLocationManager.removeUpdates(mGpsLocationListener);
                mLocationResultListener.onLocationResultAvailable(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    public boolean getLocation(Context context, LocationResultListener locationListener) {
        mLocationResultListener = locationListener;

        if (mLocationManager == null)
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!mGpsEnabled && !mNetworkEnabled)
            return false;

        if (mGpsEnabled)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpsLocationListener);

        if (mNetworkEnabled)
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkLocationListener);

        mTimer = new Timer();
        mTimer.schedule(new LastLocationFetcher(), 20000);
        return true;
    }

    private class LastLocationFetcher extends TimerTask {

        @Override
        public void run() {
            mLocationManager.removeUpdates(mGpsLocationListener);
            mLocationManager.removeUpdates(mNetworkLocationListener);

            Location gpsLoc = null, netLoc = null;
            if (mGpsEnabled)
                gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mNetworkEnabled)
                netLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLoc != null && netLoc != null) {
                if (gpsLoc.getTime() > netLoc.getTime())
                    mLocationResultListener.onLocationResultAvailable(gpsLoc);
                else
                    mLocationResultListener.onLocationResultAvailable(netLoc);
                return;
            }

            if (gpsLoc != null) {
                mLocationResultListener.onLocationResultAvailable(gpsLoc);
                return;
            }

            if (netLoc != null) {
                mLocationResultListener.onLocationResultAvailable(netLoc);
                return;
            }

            mLocationResultListener.onLocationResultAvailable(null);
        }
    }
}
