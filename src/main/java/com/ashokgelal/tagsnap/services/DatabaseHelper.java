package com.ashokgelal.tagsnap.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskListener;
import com.ashokgelal.tagsnap.model.TagInfo;
import com.ashokgelal.tagsnap.model.TagInfoAsyncTaskType;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String ID = "_id";
    public static final String DESCRITPION = "description";
    public static final String CATEGORY = "category";
    public static final String PICTURE_URI = "picture_uri";
    public static final String ADDRESS1 = "address1";
    public static final String ADDRESS2 = "address2";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private static final String TABLE_NAME = "tagsnaps";
    private static final String DATABASE_NAME = "tagsnap.db";
    private static final int SCHEME_VERSION = 1;
    private static DatabaseHelper mSingleton;

    private DatabaseHelper(Context ctxt) {
        super(ctxt, DATABASE_NAME, null, SCHEME_VERSION);
    }

    public synchronized static DatabaseHelper getInstance(Context ctxt) {
        if (mSingleton == null)
            mSingleton = new DatabaseHelper(ctxt.getApplicationContext());
        return mSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s(" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s TEXT, " +
                "%s REAL, " +
                "%s REAL)", TABLE_NAME, ID, DESCRITPION, CATEGORY, PICTURE_URI, ADDRESS1, ADDRESS2, LATITUDE, LONGITUDE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public void addNewTagInfoAsync(TagInfo tagInfo, TagInfoAsyncTaskListener listener) {
        TaskHelper.executeAsyncTask(new CreateTagInfoTask(tagInfo, listener));
    }

    public void addNewTagInfoAsync() {
    }

    private class CreateTagInfoTask extends AsyncTask<Void, Void, Void> {

        private final TagInfo mTagInfo;
        private final TagInfoAsyncTaskListener mListener;

        private CreateTagInfoTask(TagInfo tagInfo, TagInfoAsyncTaskListener listener) {
            mTagInfo = tagInfo;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues cv = new ContentValues();
            cv.put(DESCRITPION, mTagInfo.getDescription());
            cv.put(CATEGORY, mTagInfo.getCategory());
            cv.put(PICTURE_URI, mTagInfo.getPictureUri().getPath());
            cv.put(ADDRESS1, mTagInfo.getAddress1());
            cv.put(ADDRESS2, mTagInfo.getAddress2());
            cv.put(LATITUDE, mTagInfo.getLatitude());
            cv.put(LONGITUDE, mTagInfo.getLongitude());
            long id = getWritableDatabase().insert(TABLE_NAME, DESCRITPION, cv);
            mTagInfo.setId(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mListener != null)
                mListener.onAsyncTaskCompleted(mTagInfo, TagInfoAsyncTaskType.CREATE.CREATE);
        }
    }
}
