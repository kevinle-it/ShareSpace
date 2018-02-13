package com.lmtri.sharespace.api.service.user.post.housing;

import com.lmtri.sharespace.api.model.Housing;

import java.util.List;

/**
 * Created by lmtri on 12/7/2017.
 */

public interface IGetMoreOlderPostedHousingsCallback {
    void onGetComplete(List<Housing> olderPostedHousings);
    void onGetFailure(Throwable t);
}
