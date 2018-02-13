package com.lmtri.sharespace.helper.busevent.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public class AcceptShareHousingAppointmentEvent {
    private ShareHousingAppointment ShareHousingAppointment;

    public AcceptShareHousingAppointmentEvent(ShareHousingAppointment shareHousingAppointment) {
        this.ShareHousingAppointment = shareHousingAppointment;
    }

    public ShareHousingAppointment getShareHousingAppointment() {
        return ShareHousingAppointment;
    }
}
