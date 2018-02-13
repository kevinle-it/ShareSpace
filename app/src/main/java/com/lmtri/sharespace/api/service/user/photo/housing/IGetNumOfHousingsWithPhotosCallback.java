package com.lmtri.sharespace.api.service.user.photo.housing;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetNumOfHousingsWithPhotosCallback {
    void onGetComplete(Integer numOfHousingsWithPhotos);
    void onGetFailure(Throwable t);
}
