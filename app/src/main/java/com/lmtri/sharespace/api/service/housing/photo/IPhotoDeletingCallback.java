package com.lmtri.sharespace.api.service.housing.photo;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface IPhotoDeletingCallback {
    void onDeleteComplete(Boolean isDeleted);
    void onDeleteFailure(Throwable t);
}
