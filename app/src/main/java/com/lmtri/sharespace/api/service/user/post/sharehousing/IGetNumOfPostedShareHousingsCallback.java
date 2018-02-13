package com.lmtri.sharespace.api.service.user.post.sharehousing;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetNumOfPostedShareHousingsCallback {
    void onGetComplete(Integer numOfPostedShareHousings);
    void onGetFailure(Throwable t);
}
