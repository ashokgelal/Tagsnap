package com.ashokgelal.tagsnap.listeners;

import com.ashokgelal.tagsnap.model.TagInfo;

import java.util.List;

public interface TagInfoAsyncListListener {
    public void onTagInfoListAvailable(List<TagInfo> list);
}
