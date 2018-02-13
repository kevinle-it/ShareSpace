package com.lmtri.sharespace.api.service.housing.post;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface IHousingUpdatingCallback {
    void onUpdateComplete(Boolean isUpdated);
    void onUpdateFailure(Throwable t);
}
