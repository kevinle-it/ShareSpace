package com.lmtri.sharespace.helper.busevent.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public class SetNewShareHousingAppointmentEvent {
    private ShareHousingAppointment ShareHousingAppointment;

    public SetNewShareHousingAppointmentEvent(ShareHousingAppointment shareHousingAppointment) {
        this.ShareHousingAppointment = shareHousingAppointment;
    }

    public ShareHousingAppointment getShareHousingAppointment() {
        return ShareHousingAppointment;
    }
}
