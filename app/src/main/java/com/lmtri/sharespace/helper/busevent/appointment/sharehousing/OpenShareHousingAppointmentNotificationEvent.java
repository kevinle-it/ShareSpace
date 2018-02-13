package com.lmtri.sharespace.helper.busevent.appointment.sharehousing;

import com.lmtri.sharespace.api.model.AppointmentNotificationData;

/**
 * Created by lmtri on 12/26/2017.
 */

public class OpenShareHousingAppointmentNotificationEvent {
    private AppointmentNotificationData Data;

    public OpenShareHousingAppointmentNotificationEvent(AppointmentNotificationData data) {
        Data = data;
    }

    public AppointmentNotificationData getData() {
        return Data;
    }
}
