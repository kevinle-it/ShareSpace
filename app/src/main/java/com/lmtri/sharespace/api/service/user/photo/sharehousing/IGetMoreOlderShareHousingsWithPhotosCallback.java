package com.lmtri.sharespace.api.service.user.photo.sharehousing;

import com.lmtri.sharespace.api.model.HistoryShareHousingPhoto;
import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetMoreOlderShareHousingsWithPhotosCallback {
    void onGetComplete(List<HistoryShareHousingPhoto> olderShareHousingsWithPhotos);
    void onGetFailure(Throwable t);
}
