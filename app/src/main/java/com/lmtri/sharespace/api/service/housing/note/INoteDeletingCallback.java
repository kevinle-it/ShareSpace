package com.lmtri.sharespace.api.service.housing.note;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface INoteDeletingCallback {
    void onDeleteComplete(Boolean isDeleted);
    void onDeleteFailure(Throwable t);
}
