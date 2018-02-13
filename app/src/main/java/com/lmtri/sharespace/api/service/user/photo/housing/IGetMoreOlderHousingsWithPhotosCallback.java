package com.lmtri.sharespace.api.service.user.photo.housing;

import com.lmtri.sharespace.api.model.HistoryHousingPhoto;

import java.util.List;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetMoreOlderHousingsWithPhotosCallback {
    void onGetComplete(List<HistoryHousingPhoto> olderHousingsWithPhotos);
    void onGetFailure(Throwable t);
}
