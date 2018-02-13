package com.lmtri.sharespace.helper.firebasecloudmessaging;

import com.lmtri.sharespace.api.model.FCMResponse;

/**
 * Created by lmtri on 12/26/2017.
 */

public interface IFCMSendNotificationCallback {
    void onSendComplete(FCMResponse fcmResponse);
    void onSendFailure(Throwable t);
}
