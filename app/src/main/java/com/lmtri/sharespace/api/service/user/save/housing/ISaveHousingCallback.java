package com.lmtri.sharespace.api.service.user.save.housing;

import com.lmtri.sharespace.api.model.SavedHousing;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface ISaveHousingCallback {
    void onSaveComplete(SavedHousing savedHousing);
    void onSaveFailure(Throwable t);
}
