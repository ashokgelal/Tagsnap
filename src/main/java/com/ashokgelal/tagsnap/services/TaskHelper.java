package com.ashokgelal.tagsnap.services;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class TaskHelper {
    @TargetApi(11)
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            task.execute(params);
    }
}
