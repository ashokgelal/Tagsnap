package com.ashokgelal.tagsnap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.ashokgelal.tagsnap.listeners.LocationItemLongClickListener;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskCursorListener;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskListener;
import com.ashokgelal.tagsnap.model.TagInfo;
import com.ashokgelal.tagsnap.model.TagInfoAdapter;
import com.ashokgelal.tagsnap.model.TagInfoAsyncTaskType;
import com.ashokgelal.tagsnap.services.DatabaseHelper;

public class LocationsFragment extends SherlockListFragment implements TagInfoAsyncTaskCursorListener, TagInfoAsyncTaskListener {

    private static final int EDIT_DETAILS_REQUEST = 1;
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
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            getListView().setOnItemLongClickListener(new LocationItemLongClickListener(this));
        } else
            mAdapter.changeCursor(cursor);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        long tagId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID));
        DatabaseHelper.getInstance(getActivity()).getTagInfoAsync(tagId, this);
    }

    @Override
    public void onAsyncTaskCompleted(TagInfo taginfo, TagInfoAsyncTaskType type) {
        switch (type) {
            case RETRIEVE:
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("taginfo", taginfo);
                startActivityForResult(intent, EDIT_DETAILS_REQUEST);
                break;
        }
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

    public void handleDelete() {
        Cursor cursor = (Cursor) getListAdapter().getItem(getListView().getCheckedItemPosition());
        long tagId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
        DatabaseHelper.getInstance(getActivity()).deleteTagInfoAsync(tagId, this);
    }
}
