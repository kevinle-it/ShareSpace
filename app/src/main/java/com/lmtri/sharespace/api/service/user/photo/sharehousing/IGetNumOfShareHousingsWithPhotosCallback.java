package com.lmtri.sharespace.api.service.user.photo.sharehousing;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetNumOfShareHousingsWithPhotosCallback {
    void onGetComplete(Integer numOfShareHousingsWithPhotos);
    void onGetFailure(Throwable t);
}
