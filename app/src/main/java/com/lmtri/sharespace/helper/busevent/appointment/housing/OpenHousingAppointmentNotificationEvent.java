package com.lmtri.sharespace.helper.busevent.appointment.housing;

import com.lmtri.sharespace.api.model.AppointmentNotificationData;

/**
 * Created by lmtri on 12/26/2017.
 */

public class OpenHousingAppointmentNotificationEvent {
    private AppointmentNotificationData Data;

    public OpenHousingAppointmentNotificationEvent(AppointmentNotificationData data) {
        Data = data;
    }

    public AppointmentNotificationData getData() {
        return Data;
    }
}
