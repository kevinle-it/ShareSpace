package com.lmtri.sharespace.model;

import android.net.Uri;

/**
 * Created by lmtri on 6/14/2017.
 */

public class Housing {
    private String mId;
    private Uri mProfileImageUrl;
    private String mTitle;
    private String mHouseType;
    private String mPrice;
    private String mAddressHouseNumber;
    private String mAddressStreet;
    private String mAddressWard;
    private String mAddressDistrict;
    private String mAddressCity;
    private String mOwnerName;
    private String mDetailsInfo;

    public Housing(String mId, Uri mProfileImageUrl) {
        this.mId = mId;
        this.mProfileImageUrl = mProfileImageUrl;
    }

    public Housing(Uri mProfileImageUrl, String mTitle, String mHouseType, String mPrice, String mAddressHouseNumber, String mAddressStreet, String mAddressWard, String mAddressDistrict, String mAddressCity, String mOwnerName, String mDetailsInfo) {
        this.mProfileImageUrl = mProfileImageUrl;
        this.mTitle = mTitle;
        this.mHouseType = mHouseType;
        this.mPrice = mPrice;
        this.mAddressHouseNumber = mAddressHouseNumber;
        this.mAddressStreet = mAddressStreet;
        this.mAddressWard = mAddressWard;
        this.mAddressDistrict = mAddressDistrict;
        this.mAddressCity = mAddressCity;
        this.mOwnerName = mOwnerName;
        this.mDetailsInfo = mDetailsInfo;
    }

    public String getId() {
        return mId;
    }

    public Uri getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setProfileImageUrl(Uri mProfileImageUrl) {
        this.mProfileImageUrl = mProfileImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getHouseType() {
        return mHouseType;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getAddressHouseNumber() {
        return mAddressHouseNumber;
    }

    public String getAddressStreet() {
        return mAddressStreet;
    }

    public String getAddressWard() {
        return mAddressWard;
    }

    public String getAddressDistrict() {
        return mAddressDistrict;
    }

    public String getAddressCity() {
        return mAddressCity;
    }

    public String getOwnerName() {
        return mOwnerName;
    }

    public String getDetailsInfo() {
        return mDetailsInfo;
    }
}
