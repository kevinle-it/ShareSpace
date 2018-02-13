package com.lmtri.sharespace.api.service.user.save.sharehousing;

/**
 * Created by lmtri on 12/5/2017.
 */

public interface IGetNumOfSavedShareHousingsCallback {
    void onGetComplete(Integer numOfSavedShareHousings);
    void onGetFailure(Throwable t);
}
