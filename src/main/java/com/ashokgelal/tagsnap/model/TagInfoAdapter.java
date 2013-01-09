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
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Arrays;
import java.util.List;

public class TagInfoAdapter extends CursorAdapter implements StickyListHeadersAdapter {
    private final LayoutInflater mInflater;
    private final List<String> mCategories;
    private final ImageLoader mImageLoader;

    public TagInfoAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
        mCategories = Arrays.asList(context.getResources().getStringArray(R.array.categories_array));
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
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
        ImageView imageView = (ImageView)view.findViewById(R.id.locationImage);
        mImageLoader.displayImage(String.format("file://%s", picturePath), imageView);

    }

    @Override
    public long getHeaderId(int position) {
        Cursor cursor = (Cursor) getItem(position);
        String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY));
        return mCategories.indexOf(category);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.headerText);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        Cursor cursor = (Cursor) getItem(position);
        String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY));
        char headerChar = category.subSequence(0, 1).charAt(0);
        holder.text.setText(headerChar + "");
        return convertView;
    }

    private static class HeaderViewHolder {
        TextView text;
    }
}
