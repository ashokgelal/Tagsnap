package com.ashokgelal.tagsnap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncListListener;
import com.ashokgelal.tagsnap.model.TagInfo;
import com.ashokgelal.tagsnap.services.DatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class MapFragment extends SupportMapFragment implements TagInfoAsyncListListener, GoogleMap.OnInfoWindowClickListener {

    private static final int EDIT_DETAILS_REQUEST = 1;
    private Dictionary<String, TagInfo> mMarkerDict;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMarkerDict = new Hashtable<String, TagInfo>();
        getMap().setOnInfoWindowClickListener(this);
        addMarkersFromDatabase();
    }

    private void addMarkersFromDatabase() {
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
        db.fetchTagInfoListAsync(this);
    }

    @Override
    public void onTagInfoListAvailable(List<TagInfo> list) {
        getMap().clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (TagInfo tagInfo : list) {
            Marker marker = addMarker(tagInfo);
            mMarkerDict.put(marker.getId(), tagInfo);
            builder.include(marker.getPosition());
        }

        if (list.size() > 0)
            getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }

    private Marker addMarker(TagInfo tagInfo) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(tagInfo.getLatitude(), tagInfo.getLongitude()));
        options.title(tagInfo.getDescription());
        options.snippet(tagInfo.getAddress1());
        return getMap().addMarker(options);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        TagInfo tagInfo = mMarkerDict.get(marker.getId());
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("taginfo", tagInfo);
        startActivityForResult(intent, EDIT_DETAILS_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_DETAILS_REQUEST) {
                TagInfo tagInfo = data.getParcelableExtra("taginfo");
                final DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
                db.updateTagInfoAsync(tagInfo, this);
            }
        }
    }
}
