package com.ashokgelal.tagsnap.listeners;

import android.view.View;
import android.widget.AdapterView;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ashokgelal.tagsnap.LocationsFragment;
import com.ashokgelal.tagsnap.R;

public class LocationItemLongClickListener implements AdapterView.OnItemLongClickListener, ActionMode.Callback {

    private final LocationsFragment mHostFragment;
    private ActionMode mActionMode;

    public LocationItemLongClickListener(LocationsFragment hostFragment) {
        mHostFragment = hostFragment;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        mHostFragment.getListView().clearChoices();
        mHostFragment.getListView().setItemChecked(position, true);
        if (mActionMode == null)
            mActionMode = mHostFragment.getSherlockActivity().startActionMode(this);

        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mHostFragment.getSherlockActivity().getSupportMenuInflater();
        inflater.inflate(R.menu.location_selected, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            mHostFragment.handleDelete();
            mActionMode.finish();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
        mHostFragment.getListView().clearChoices();
        mHostFragment.getListView().requestLayout();
    }
}
