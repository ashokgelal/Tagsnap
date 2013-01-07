package com.ashokgelal.tagsnap;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ashokgelal.tagsnap.listeners.AddressResultListener;
import com.ashokgelal.tagsnap.listeners.TabListener;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskListener;
import com.ashokgelal.tagsnap.model.TagInfo;
import com.ashokgelal.tagsnap.model.TagInfoAsyncTaskType;
import com.ashokgelal.tagsnap.services.DatabaseHelper;

public class DefaultActivity extends SherlockFragmentActivity implements AddressResultListener, TagInfoAsyncTaskListener {
    private static final int ADD_DETAILS_REQUEST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        addTabs();
        if (savedInstanceState != null) {
            // restore selected tab index from previous 'session'
            int index = savedInstanceState.getInt("selected_tab_index", 0);
            getSupportActionBar().setSelectedNavigationItem(index);
        }
    }

    private void addTabs() {
        // get support ActionBar and set navigation mode to Tabs
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Add CURRENT tab to ActionBar, and set the TabListener to a new TabListner object
        String currentTitle = getResources().getString(R.string.current);
        ActionBar.Tab currentTab = bar.newTab();
        currentTab.setText(currentTitle);
        currentTab.setTabListener(new TabListener(this, currentTitle, CurrentFragment.class));
        bar.addTab(currentTab);

        // Add LOCATIONS tab to ActionBar, and set the TabListener to a new TabListner object
        String locationsTitle = getResources().getString(R.string.locations);
        ActionBar.Tab locationsTab = bar.newTab();
        locationsTab.setText(locationsTitle);
        locationsTab.setTabListener(new TabListener(this, locationsTitle, LocationsFragment.class));
        bar.addTab(locationsTab);

        // Add MAP tab to ActionBar, and set the TabListener to a new TabListner object
        String mapTitle = getResources().getString(R.string.map);
        ActionBar.Tab mapTab = bar.newTab();
        mapTab.setText(mapTitle);
        mapTab.setTabListener(new TabListener(this, mapTitle, MapFragment.class));
        bar.addTab(mapTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save tab index to restore it later after config changes
        int index = getSupportActionBar().getSelectedNavigationIndex();
        outState.putInt("selected_tab_index", index);
    }

    @Override
    public void onAddressAvailable(Address address) {
        Intent intent = new Intent(this, DetailsActivity.class);
        TagInfo tagsnap = new TagInfo();
        if (address.getMaxAddressLineIndex() > 0)
            tagsnap.setAddress1(address.getAddressLine(0));

        tagsnap.setAddress2(String.format("%s, %s, %s", address.getLocality(), address.getAdminArea(), address.getCountryName()));
        tagsnap.setLatitude(address.getLatitude());
        tagsnap.setLongitude(address.getLongitude());
        intent.putExtra("taginfo", tagsnap);
        startActivityForResult(intent, ADD_DETAILS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DETAILS_REQUEST) {
            if (resultCode == RESULT_OK) {
                TagInfo tagsnap = data.getParcelableExtra("taginfo");
                DatabaseHelper db = DatabaseHelper.getInstance(this);
                db.addNewTagInfoAsync(tagsnap, this);
            }
        }
    }

    @Override
    public void onAsyncTaskCompleted(TagInfo taginfo, TagInfoAsyncTaskType type) {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.close();
        Toast.makeText(this, "Successfully added new TagInfo object to the database", Toast.LENGTH_LONG).show();
    }
}
