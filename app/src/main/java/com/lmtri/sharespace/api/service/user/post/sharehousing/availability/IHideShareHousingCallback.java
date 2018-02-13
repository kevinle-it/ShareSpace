package com.lmtri.sharespace.api.service.user.post.sharehousing.availability;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IHideShareHousingCallback {
    void onHideComplete(Boolean isSuccess);
    void onHideFailure(Throwable t);
}
