package com.ashokgelal.tagsnap;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ashokgelal.tagsnap.listeners.AddressResultListener;
import com.ashokgelal.tagsnap.listeners.LocationResultListener;
import com.ashokgelal.tagsnap.services.LocationService;
import com.ashokgelal.tagsnap.services.ReverseGeocodingService;

public class CurrentFragment extends SherlockFragment implements LocationResultListener, AddressResultListener, View.OnClickListener {
    private LocationService mLocationService;
    private ImageView mLocationIcon;
    private ImageButton mTagButton;
    private TextView mAddress1;
    private TextView mAddress2;
    private TextView mLat;
    private TextView mLon;
    private Address mLastKnownAddress;
    private AddressResultListener mAddressResultListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_frag, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mAddressResultListener = (AddressResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddressResultListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // tells that we have a menu item to add to the ActionBar
        setHasOptionsMenu(true);
        mLocationIcon = (ImageView) getView().findViewById(R.id.locationIcon);
        mTagButton = (ImageButton) getView().findViewById(R.id.tagButton);
        mAddress1 = (TextView) getView().findViewById(R.id.address1);
        mAddress2 = (TextView) getView().findViewById(R.id.address2);
        mLat = (TextView) getView().findViewById(R.id.latitude);
        mLon = (TextView) getView().findViewById(R.id.longitude);
        mTagButton.setOnClickListener(this);
        // restore last known address
        if (savedInstanceState != null)
            mLastKnownAddress = savedInstanceState.getParcelable("last_known_address");
        if (mLastKnownAddress != null)
            setAddressDetails(mLastKnownAddress);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.current_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.find_current_location) {
            if (mLocationService == null)
                mLocationService = new LocationService();
            mLocationService.getLocation(getActivity(), this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationResultAvailable(Location location) {
        if (location == null) {
            // TODO: notify user
        } else {
            // we have a location, reverse geocode it
            new ReverseGeocodingService(getActivity(), this).execute(location);
        }
    }

    @Override
    public void onAddressAvailable(Address address) {
        if (address == null) {
            // TODO: notify user
        } else {
            // we have an address, save it and display the details
            mLastKnownAddress = address;
            setAddressDetails(address);
        }
    }

    private void setAddressDetails(Address address) {
        // if we have the first address line
        if (address.getMaxAddressLineIndex() > 0)
            mAddress1.setText(address.getAddressLine(0));

        mAddress2.setText(String.format("%s, %s, %s", address.getLocality(), address.getAdminArea(), address.getCountryName()));
        Resources res = getResources();
        mLat.setText(String.format(res.getString(R.string.lat_val), address.getLatitude()));
        mLon.setText(String.format(res.getString(R.string.lon_val), address.getLongitude()));

        mTagButton.setImageResource(R.drawable.tag_button);
        mLocationIcon.setImageResource(R.drawable.known_location);
        // the tag button is now clickable because we have an address
        mTagButton.setClickable(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the last known address before the config changes
        if (mLastKnownAddress != null)
            outState.putParcelable("last_known_address", mLastKnownAddress);
    }

    @Override
    public void onClick(View view) {
        if (mAddressResultListener != null && mLastKnownAddress != null)
            mAddressResultListener.onAddressAvailable(mLastKnownAddress);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLocationService != null)
            mLocationService.stop();
    }
}
