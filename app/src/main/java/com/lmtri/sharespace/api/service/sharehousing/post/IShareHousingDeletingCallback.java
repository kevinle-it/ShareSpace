package com.lmtri.sharespace.api.service.sharehousing.post;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IShareHousingDeletingCallback {
    void onDeleteComplete(Boolean isDeleted);
    void onDeleteFailure(Throwable t);
}
