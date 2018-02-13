package com.lmtri.sharespace.helper.firebasestorage.sharehousing;

/**
 * Created by lmtri on 11/29/2017.
 */

public interface OnShareHousingUpdatingListener {
    void onUpdateComplete(Boolean isUpdated);
    void onUpdateFailure(Throwable t);
}
