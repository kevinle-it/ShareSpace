package com.lmtri.sharespace.helper.busevent.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public class UpdateShareHousingAppointmentEvent {
    private ShareHousingAppointment ShareHousingAppointment;

    public UpdateShareHousingAppointmentEvent(ShareHousingAppointment shareHousingAppointment) {
        this.ShareHousingAppointment = shareHousingAppointment;
    }

    public ShareHousingAppointment getShareHousingAppointment() {
        return ShareHousingAppointment;
    }
}
