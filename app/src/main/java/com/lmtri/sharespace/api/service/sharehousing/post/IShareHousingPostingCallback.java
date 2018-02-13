package com.lmtri.sharespace.api.service.sharehousing.post;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IShareHousingPostingCallback {
    void onPostComplete(ShareHousing shareHousing);
    void onPostFailure(Throwable t);
}
