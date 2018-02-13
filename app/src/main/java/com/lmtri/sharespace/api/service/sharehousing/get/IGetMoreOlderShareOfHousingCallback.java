package com.lmtri.sharespace.api.service.sharehousing.get;

import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

/**
 * Created by lmtri on 8/22/2017.
 */

public interface IGetMoreOlderShareOfHousingCallback {
    void onGetComplete(List<ShareHousing> shareHousings);
    void onGetFailure(Throwable t);
}
