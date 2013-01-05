package com.ashokgelal.tagsnap.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class TagInfo implements Parcelable {
    private String mDescription;
    private String mCategory;
    private Uri mPictureUri;
    private String mAddress1;
    private String mAddress2;
    private double mLatitude;
    private double mLongitude;
    private long mId;

    public static final Creator<TagInfo> CREATOR = new Creator<TagInfo>() {
        @Override
        public TagInfo createFromParcel(Parcel parcel) {
            return new TagInfo(parcel);
        }

        @Override
        public TagInfo[] newArray(int size) {
            return new TagInfo[size];
        }

    };

    public TagInfo(long id) {
        mId = id;
    }

    public TagInfo() {
        this(-1);
    }

    public TagInfo(Parcel data) {
        setId(data.readLong());
        setDescription(data.readString());
        setCategory(data.readString());
        setPictureUri(data.readString());
        setAddress1(data.readString());
        setAddress2(data.readString());
        setLatitude(data.readDouble());
        setLongitude(data.readDouble());
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public Uri getPictureUri() {
        return mPictureUri;
    }

    public void setPictureUri(Uri pictureUri) {
        this.mPictureUri = pictureUri;
    }

    public void setPictureUri(String uri) {
        setPictureUri(Uri.parse(uri));
    }

    public String getAddress1() {
        return mAddress1;
    }

    public void setAddress1(String address1) {
        this.mAddress1 = address1;
    }

    public String getAddress2() {
        return mAddress2;
    }

    public void setAddress2(String address2) {
        this.mAddress2 = address2;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId());
        parcel.writeString(getDescription());
        parcel.writeString(getCategory());
        if (getPictureUri() == null)
            parcel.writeString("");
        else
            parcel.writeString(getPictureUri().getPath());
        parcel.writeString(getAddress1());
        parcel.writeString(getAddress2());
        parcel.writeDouble(getLatitude());
        parcel.writeDouble(getLongitude());
    }
}
