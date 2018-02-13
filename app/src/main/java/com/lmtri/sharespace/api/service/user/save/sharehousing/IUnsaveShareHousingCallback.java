package com.lmtri.sharespace.api.service.user.save.sharehousing;

import com.lmtri.sharespace.api.model.SavedShareHousing;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IUnsaveShareHousingCallback {
    void onUnsaveComplete(SavedShareHousing savedShareHousing);
    void onUnsaveFailure(Throwable t);
}
