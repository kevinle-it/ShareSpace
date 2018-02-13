package com.lmtri.sharespace.api.service.user.save.housing;

import com.lmtri.sharespace.api.model.SavedHousing;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IUnsaveHousingCallback {
    void onUnsaveComplete(SavedHousing savedHousing);
    void onUnsaveFailure(Throwable t);
}
