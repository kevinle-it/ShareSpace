package com.lmtri.sharespace.helper.firebasestorage.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 8/23/2017.
 */

public interface OnShareHousingPostingListener {
    void onPostSuccess(ShareHousing shareHousing);
    void onPostFailure(Throwable t);
}
