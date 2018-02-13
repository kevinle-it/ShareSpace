package com.lmtri.sharespace.api.service.user.save.sharehousing;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IGetSavingStateOfCurrentShareHousingCallback {
    void onGetComplete(Boolean isSaved);
    void onGetFailure(Throwable t);
}
