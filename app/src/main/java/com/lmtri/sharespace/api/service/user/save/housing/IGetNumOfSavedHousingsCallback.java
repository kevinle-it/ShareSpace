package com.lmtri.sharespace.api.service.user.save.housing;

/**
 * Created by lmtri on 12/5/2017.
 */

public interface IGetNumOfSavedHousingsCallback {
    void onGetComplete(Integer numOfSavedHousings);
    void onGetFailure(Throwable t);
}
