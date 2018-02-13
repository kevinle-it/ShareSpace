package com.lmtri.sharespace.api.service.housing.photo;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface IPhotoUpdatingCallback {
    void onUpdateComplete(Boolean isUpdated);
    void onUpdateFailure(Throwable t);
}
