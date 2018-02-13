package com.lmtri.sharespace.helper.firebasestorage.housing;

/**
 * Created by lmtri on 11/29/2017.
 */

public interface OnHousingDeletingListener {
    void onDeleteComplete(Boolean isDeleted);
    void onDeleteFailure(Throwable t);
}
