package com.lmtri.sharespace.api.service.user.post.sharehousing.availability;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetHidingStateOfCurrentShareHousingCallback {
    void onGetComplete(Boolean isHidden);
    void onGetFailure(Throwable t);
}
