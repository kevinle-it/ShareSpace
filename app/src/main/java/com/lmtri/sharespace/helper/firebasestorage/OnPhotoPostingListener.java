package com.lmtri.sharespace.helper.firebasestorage;

/**
 * Created by lmtri on 8/23/2017.
 */

public interface OnPhotoPostingListener {
    void onPostSuccess(String photoURL);
    void onPostFailure(Throwable t);
}
