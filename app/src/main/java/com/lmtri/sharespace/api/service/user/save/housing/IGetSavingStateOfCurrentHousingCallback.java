package com.lmtri.sharespace.api.service.user.save.housing;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IGetSavingStateOfCurrentHousingCallback {
    void onGetComplete(Boolean isSaved);
    void onGetFailure(Throwable t);
}
