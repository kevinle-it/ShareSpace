package com.lmtri.sharespace.api.service.user;

import com.lmtri.sharespace.api.model.User;

/**
 * Created by lmtri on 11/30/2017.
 */

public interface IGetUserInfoCallback {
    void onGetUserInfoSuccess(User user);
    void onGetUserInfoFailure(Throwable t);
}
