package com.lmtri.sharespace.helper.firebasecloudmessaging;

import com.lmtri.sharespace.api.model.AppointmentNotification;
import com.lmtri.sharespace.api.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by lmtri on 12/26/2017.
 */

public interface IFCMClient {
    @POST("send")
    Call<FCMResponse> sendNotification(@Body AppointmentNotification data);
}
