package com.lmtri.sharespace.api.service.user.save.sharehousing;

import com.lmtri.sharespace.api.model.SavedShareHousing;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface ISaveShareHousingCallback {
    void onSaveComplete(SavedShareHousing savedShareHousing);
    void onSaveFailure(Throwable t);
}
