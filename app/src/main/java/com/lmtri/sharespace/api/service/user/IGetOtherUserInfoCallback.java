package com.lmtri.sharespace.api.service.user;

import com.lmtri.sharespace.api.model.User;

/**
 * Created by lmtri on 12/26/2017.
 */

public interface IGetOtherUserInfoCallback {
    void onGetComplete(User otherUser);
    void onGetFailure(Throwable t);
}
