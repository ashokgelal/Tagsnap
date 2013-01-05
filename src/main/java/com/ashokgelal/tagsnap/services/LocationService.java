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
        // create a GPS location listener
        mGpsLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                stop();
                // callback
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

        // create a Network location listener
        mNetworkLocationListener = new

                LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        stop();
                        // callback
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

        // if none are availabe, there is no way to get the location
        if (!mGpsEnabled && !mNetworkEnabled)
            return false;

        if (mGpsEnabled)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGpsLocationListener);

        if (mNetworkEnabled)
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkLocationListener);

        // set a timer that will fire in 20 seconds but only if we can't get the current location. Otherwise it will
        // be cancelled
        mTimer = new Timer();
        mTimer.schedule(new LastLocationFetcher(), 20000);
        return true;
    }

    public void stop() {
        if (mTimer != null)
            mTimer.cancel();
        // stop from getting further updates
        mLocationManager.removeUpdates(mGpsLocationListener);
        mLocationManager.removeUpdates(mNetworkLocationListener);
    }

    private class LastLocationFetcher extends TimerTask {

        @Override
        public void run() {
            // the timer is fired; we waited enough; we no longer want the current location
            mLocationManager.removeUpdates(mGpsLocationListener);
            mLocationManager.removeUpdates(mNetworkLocationListener);

            // get the last known location instead
            Location gpsLoc = null, netLoc = null;
            if (mGpsEnabled)
                gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mNetworkEnabled)
                netLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // check the latest location if we have locations from both radios
            if (gpsLoc != null && netLoc != null) {
                if (gpsLoc.getTime() > netLoc.getTime())
                    mLocationResultListener.onLocationResultAvailable(gpsLoc);
                else
                    mLocationResultListener.onLocationResultAvailable(netLoc);
                return;
            }

            // if we have the location from only GPS, use it
            if (gpsLoc != null) {
                mLocationResultListener.onLocationResultAvailable(gpsLoc);
                return;
            }

            // if we have the location from only Wi-Fi, use it
            if (netLoc != null) {
                mLocationResultListener.onLocationResultAvailable(netLoc);
                return;
            }

            // last known location is not avaiable
            mLocationResultListener.onLocationResultAvailable(null);
        }
    }
}
