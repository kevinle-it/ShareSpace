package com.lmtri.sharespace.api.service.housing.post;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface IHousingDeletingCallback {
    void onDeleteComplete(Boolean isDeleted);
    void onDeleteFailure(Throwable t);
}
