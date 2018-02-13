package com.lmtri.sharespace.api.service.user.post.housing;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetNumOfPostedHousingsCallback {
    void onGetComplete(Integer numOfPostedHousings);
    void onGetFailure(Throwable t);
}
