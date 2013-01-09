package com.ashokgelal.tagsnap;

import android.database.Cursor;
import com.actionbarsherlock.app.SherlockListFragment;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskCursorListener;
import com.ashokgelal.tagsnap.model.TagInfoAdapter;
import com.ashokgelal.tagsnap.services.DatabaseHelper;

public class LocationsFragment extends SherlockListFragment implements TagInfoAsyncTaskCursorListener {

    private TagInfoAdapter mAdapter;

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
