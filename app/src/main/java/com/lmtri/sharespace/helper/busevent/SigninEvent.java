package com.lmtri.sharespace.helper.busevent;

import com.lmtri.sharespace.api.model.User;

/**
 * Created by lmtri on 11/28/2017.
 */

public class SigninEvent {
    private User user;

    public SigninEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
