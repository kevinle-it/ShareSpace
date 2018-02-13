package com.lmtri.sharespace.api.service.housing.post;

import com.lmtri.sharespace.api.model.Housing;

/**
 * Created by lmtri on 8/16/2017.
 */

public interface IHousingPostingCallback {
    void onPostComplete(Housing housing);
    void onPostFailure(Throwable t);
}
