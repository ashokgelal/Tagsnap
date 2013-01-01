package com.ashokgelal.tagsnap.listeners;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;

public class TabListener implements ActionBar.TabListener {

    private final FragmentActivity mActivity;
    private final String mTag;
    private final Class mFragmentClass;
    private Fragment mFragment;

    public TabListener(FragmentActivity activity, String tag, Class fragmentClass) {
        mActivity = activity;
        mTag = tag;
        mFragmentClass = fragmentClass;
        // see if we already have the fragment with given tag. it's okay if it is null
        mFragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            // instantiate a new fragment for the given class
            mFragment = SherlockFragment.instantiate(mActivity, mFragmentClass.getName());
            // place in the default root viewgroup - android.R.id.content
            ft.replace(android.R.id.content, mFragment, mTag);
        } else {
            if (mFragment.isDetached())
                ft.attach(mFragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}
