package com.lmtri.sharespace.api.service.housing.photo;

/**
 * Created by lmtri on 8/19/2017.
 */

public interface IPhotoPostingCallback {
    void onPostComplete(Boolean isSuccess);
    void onPostFailure(Throwable t);
}
