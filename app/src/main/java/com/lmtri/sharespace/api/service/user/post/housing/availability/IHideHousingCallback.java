package com.lmtri.sharespace.api.service.user.post.housing.availability;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IHideHousingCallback {
    void onHideComplete(Boolean isSuccess);
    void onHideFailure(Throwable t);
}
