package com.lmtri.sharespace.helper.firebasestorage.housing;

import com.lmtri.sharespace.api.model.Housing;

/**
 * Created by lmtri on 8/16/2017.
 */

public interface OnHousingPostingListener {
    void onPostSuccess(Housing housing);
    void onPostFailure(Throwable t);
}
