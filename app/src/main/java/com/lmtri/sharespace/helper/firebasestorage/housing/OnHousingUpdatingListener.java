package com.lmtri.sharespace.helper.firebasestorage.housing;

/**
 * Created by lmtri on 11/29/2017.
 */

public interface OnHousingUpdatingListener {
    void onUpdateComplete(Boolean isUpdated);
    void onUpdateFailure(Throwable t);
}
