package com.lmtri.sharespace.api.service.sharehousing.post;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IShareHousingUpdatingCallback {
    void onUpdateComplete(Boolean isUpdated);
    void onUpdateFailure(Throwable t);
}
