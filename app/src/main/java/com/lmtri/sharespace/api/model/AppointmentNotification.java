package com.lmtri.sharespace.api.model;

import java.util.List;

/**
 * Created by lmtri on 12/26/2017.
 */

public class AppointmentNotification {
    private AppointmentNotificationData data;
    private String[] registration_ids;

    public AppointmentNotification(AppointmentNotificationData data, String[] registration_ids) {
        this.data = data;
        this.registration_ids = registration_ids;
    }
}
