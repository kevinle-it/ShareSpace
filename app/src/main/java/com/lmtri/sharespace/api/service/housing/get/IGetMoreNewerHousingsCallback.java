package com.lmtri.sharespace.api.service.housing.get;

import com.lmtri.sharespace.api.model.Housing;

import java.util.List;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IGetMoreNewerHousingsCallback {
    void onGetComplete(List<Housing> newerHousings);
    void onGetFailure(Throwable t);
}
