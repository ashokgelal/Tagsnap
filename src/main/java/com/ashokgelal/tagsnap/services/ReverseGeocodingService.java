package com.ashokgelal.tagsnap.services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import com.ashokgelal.tagsnap.listeners.AddressResultListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReverseGeocodingService extends AsyncTask<Location, Void, Void> {

    private final AddressResultListener mListener;
    private final Context mContext;
    private Address mAddress;

    public ReverseGeocodingService(Context context, AddressResultListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Location... locations) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        Location loc = locations[0];
        List<Address> addresses = null;
        try {
            // get all the addresses fro the given latitude, and longitude
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            mAddress = null;
        }

        // if we have at least one address, use it
        if (addresses != null && addresses.size() > 0) {
            mAddress = addresses.get(0);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // set the address on the UI thread
        mListener.onAddressAvailable(mAddress);
    }
}
