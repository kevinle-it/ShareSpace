package com.lmtri.sharespace.api.service.user.post.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetMoreOlderPostedShareHousingsCallback {
    void onGetComplete(List<ShareHousing> olderPostedShareHousings);
    void onGetFailure(Throwable t);
}
