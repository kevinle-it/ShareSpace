package com.lmtri.sharespace.api.service.user.post.housing.availability;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetHidingStateOfCurrentHousingCallback {
    void onGetComplete(Boolean isHidden);
    void onGetFailure(Throwable t);
}
