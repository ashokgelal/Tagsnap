package com.ashokgelal.tagsnap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockListFragment;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskCursorListener;
import com.ashokgelal.tagsnap.model.TagInfoAdapter;
import com.ashokgelal.tagsnap.services.DatabaseHelper;

public class LocationsFragment extends SherlockListFragment implements TagInfoAsyncTaskCursorListener {

    private TagInfoAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sticky_header_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
        db.loadCursorAsync(this);
    }

    @Override
    public void onCursorAvailable(Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new TagInfoAdapter(getActivity(), cursor, false);
            setListAdapter(mAdapter);
        } else
            mAdapter.changeCursor(cursor);
    }
}
