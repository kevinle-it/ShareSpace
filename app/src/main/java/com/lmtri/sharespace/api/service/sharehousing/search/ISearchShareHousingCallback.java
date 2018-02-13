package com.lmtri.sharespace.api.service.sharehousing.search;

import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

/**
 * Created by lmtri on 12/20/2017.
 */

public interface ISearchShareHousingCallback {
    void onSearchComplete(List<ShareHousing> shareHousingResults);
    void onSearchFailure(Throwable t);
}
