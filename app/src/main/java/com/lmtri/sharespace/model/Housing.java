package com.lmtri.sharespace.model;

import android.net.Uri;

/**
 * Created by lmtri on 6/14/2017.
 */

public class Housing {
    private String mId;
    private Uri mProfileImageUrl;

    public Housing(String mId, Uri mProfileImageUrl) {
        this.mId = mId;
        this.mProfileImageUrl = mProfileImageUrl;
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
}
