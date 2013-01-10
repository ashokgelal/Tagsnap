package com.ashokgelal.tagsnap.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncListListener;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskCursorListener;
import com.ashokgelal.tagsnap.listeners.TagInfoAsyncTaskListener;
import com.ashokgelal.tagsnap.model.TagInfo;
import com.ashokgelal.tagsnap.model.TagInfoAsyncTaskType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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

    private static TagInfo createTagInfoFromCurrentCursorPosition(Cursor cursor) {
        long id = cursor.getLong(0);
        String description = cursor.getString(1);
        String category = cursor.getString(2);
        String uri = cursor.getString(3);
        String address1 = cursor.getString(4);
        String address2 = cursor.getString(5);
        double latitude = cursor.getDouble(6);
        double longitude = cursor.getDouble(7);

        TagInfo tagsnap = new TagInfo(id);
        tagsnap.setDescription(description);
        tagsnap.setCategory(category);
        tagsnap.setPictureUri(Uri.fromFile(new File(uri)));
        tagsnap.setAddress1(address1);
        tagsnap.setAddress2(address2);
        tagsnap.setLatitude(latitude);
        tagsnap.setLongitude(longitude);
        return tagsnap;
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

    public void getTagInfoAsync(long id, TagInfoAsyncTaskListener listener) {
        TaskHelper.executeAsyncTask(new RetrieveTagInfoTask(listener), id);
    }

    public void updateTagInfoAsync(TagInfo tagInfo, TagInfoAsyncTaskCursorListener listener) {
        TaskHelper.executeAsyncTask(new UpdateTagInfoTask(tagInfo, listener));
    }

    public void updateTagInfoAsync(TagInfo tagInfo, final TagInfoAsyncListListener listener) {
        TaskHelper.executeAsyncTask(new UpdateTagInfoTask(tagInfo, new TagInfoAsyncTaskCursorListener() {
            @Override
            public void onCursorAvailable(Cursor cursor) {
                List<TagInfo> tagInfoList = createTagInfoListFromCurrentCursor(cursor);
                cursor.close();
                listener.onTagInfoListAvailable(tagInfoList);
            }
        }));
    }

    public void deleteTagInfoAsync(long id, TagInfoAsyncTaskCursorListener listener) {
        TaskHelper.executeAsyncTask(new DeleteTagInfoTask(listener), id);
    }

    public void loadCursorAsync(TagInfoAsyncTaskCursorListener listener) {
        TaskHelper.executeAsyncTask(new LoadCursorTask(listener));
    }

    public void fetchTagInfoListAsync(final TagInfoAsyncListListener listener) {
        loadCursorAsync(new TagInfoAsyncTaskCursorListener() {
            @Override
            public void onCursorAvailable(Cursor cursor) {
                List<TagInfo> list = createTagInfoListFromCurrentCursor(cursor);
                cursor.close();
                listener.onTagInfoListAvailable(list);
            }
        });
    }

    private List<TagInfo> createTagInfoListFromCurrentCursor(Cursor cursor) {
        List<TagInfo> tagInfoList = new LinkedList<TagInfo>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tagInfoList.add(createTagInfoFromCurrentCursorPosition(cursor));
            cursor.moveToNext();
        }
        return tagInfoList;
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

    private class LoadCursorTask extends AsyncTask<Void, Void, Void> {
        private Cursor cursor;
        private TagInfoAsyncTaskCursorListener mListener;

        private LoadCursorTask(TagInfoAsyncTaskCursorListener listener) {
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cursor = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, CATEGORY);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mListener.onCursorAvailable(cursor);
        }
    }

    private class UpdateTagInfoTask extends AsyncTask<Void, Void, Void> {

        private final TagInfo mTagInfo;
        private final TagInfoAsyncTaskCursorListener mListener;

        private UpdateTagInfoTask(TagInfo taginfo, TagInfoAsyncTaskCursorListener listener) {
            mTagInfo = taginfo;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues cv = new ContentValues();
            cv.put(ID, mTagInfo.getId());
            cv.put(DESCRITPION, mTagInfo.getDescription());
            cv.put(CATEGORY, mTagInfo.getCategory());
            cv.put(PICTURE_URI, mTagInfo.getPictureUri().getPath());
            cv.put(ADDRESS1, mTagInfo.getAddress1());
            cv.put(ADDRESS2, mTagInfo.getAddress2());
            cv.put(LATITUDE, mTagInfo.getLatitude());
            cv.put(LONGITUDE, mTagInfo.getLongitude());
            long id = getWritableDatabase().replace(TABLE_NAME, DESCRITPION, cv);
            mTagInfo.setId(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadCursorAsync(mListener);
        }
    }

    private class DeleteTagInfoTask extends AsyncTask<Long, Void, Void> {

        private final TagInfoAsyncTaskCursorListener mListener;

        private DeleteTagInfoTask(TagInfoAsyncTaskCursorListener listener) {
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Long... integers) {
            getWritableDatabase().delete(TABLE_NAME, ID + "=?", new String[]{String.valueOf(integers[0])});
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadCursorAsync(mListener);
        }
    }

    private class RetrieveTagInfoTask extends AsyncTask<Long, Void, TagInfo> {
        private final TagInfoAsyncTaskListener mListener;

        private RetrieveTagInfoTask(TagInfoAsyncTaskListener listener) {
            mListener = listener;
        }

        @Override
        protected void onPostExecute(TagInfo tagInfo) {
            mListener.onAsyncTaskCompleted(tagInfo, TagInfoAsyncTaskType.RETRIEVE);
        }

        @Override
        protected TagInfo doInBackground(Long... params) {
            String[] args = {params[0].toString()};
            String query = String.format("SELECT * FROM %s WHERE _id=?", TABLE_NAME);
            Cursor cursor = getReadableDatabase().rawQuery(query, args);
            cursor.moveToFirst();
            TagInfo tagInfo = null;
            if (!cursor.isAfterLast())
                tagInfo = createTagInfoFromCurrentCursorPosition(cursor);
            cursor.close();
            return tagInfo;
        }
    }
}
