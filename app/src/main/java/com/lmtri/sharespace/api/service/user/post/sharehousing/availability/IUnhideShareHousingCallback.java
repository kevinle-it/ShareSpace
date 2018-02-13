package com.lmtri.sharespace.api.service.user.post.sharehousing.availability;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IUnhideShareHousingCallback {
    void onUnhideComplete(Boolean isSuccess);
    void onUnhideFailure(Throwable t);
}
