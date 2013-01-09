package com.ashokgelal.tagsnap.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ashokgelal.tagsnap.R;
import com.ashokgelal.tagsnap.services.DatabaseHelper;

public class TagInfoAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;

    public TagInfoAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.location_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView description = (TextView)view.findViewById(R.id.locationDescription);
        description.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCRITPION)));

        TextView category = (TextView)view.findViewById(R.id.locationCategory);
        category.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY)));

        TextView address1 = (TextView)view.findViewById(R.id.locationAddress1);
        address1.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS1)));

        TextView address2 = (TextView)view.findViewById(R.id.locationAddress2);
        address2.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS2)));

        String picturePath = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PICTURE_URI));
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picturePath), 100, 100);
        ImageView imageView = (ImageView)view.findViewById(R.id.locationImage);
        imageView.setImageBitmap(thumbnail);
    }
}
