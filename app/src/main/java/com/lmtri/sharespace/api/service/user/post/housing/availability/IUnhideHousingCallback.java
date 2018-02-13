package com.lmtri.sharespace.api.service.user.post.housing.availability;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IUnhideHousingCallback {
    void onUnhideComplete(Boolean isSuccess);
    void onUnhideFailure(Throwable t);
}
