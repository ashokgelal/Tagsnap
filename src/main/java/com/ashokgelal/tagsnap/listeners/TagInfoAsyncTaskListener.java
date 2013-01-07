package com.ashokgelal.tagsnap.listeners;

import com.ashokgelal.tagsnap.model.TagInfo;
import com.ashokgelal.tagsnap.model.TagInfoAsyncTaskType;

public interface TagInfoAsyncTaskListener {
    public void onAsyncTaskCompleted(TagInfo taginfo, TagInfoAsyncTaskType type);
}
