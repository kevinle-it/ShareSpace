package com.lmtri.sharespace.helper.firebasestorage.sharehousing;

/**
 * Created by lmtri on 11/29/2017.
 */

public interface OnShareHousingDeletingListener {
    void onDeleteComplete(Boolean isDeleted);
    void onDeleteFailure(Throwable t);
}
