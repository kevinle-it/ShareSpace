package com.lmtri.sharespace.api.service.user.save.housing;

import com.lmtri.sharespace.api.model.SavedHousing;

import java.util.List;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IGetMoreNewerSavedHousingsCallback {
    void onGetComplete(List<SavedHousing> newerSavedHousings);
    void onGetFailure(Throwable t);
}
