package com.lmtri.sharespace.api.service.sharehousing.get;

/**
 * Created by lmtri on 11/29/2017.
 */

public interface ICheckIfExistSharePostsOfCurrentHousingCallback {
    void onCheckComplete(Boolean isExist);
    void onCheckFailure(Throwable t);
}
