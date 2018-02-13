package com.lmtri.sharespace.helper.busevent.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public class AcceptHousingAppointmentEvent {
    private HousingAppointment HousingAppointment;

    public AcceptHousingAppointmentEvent(HousingAppointment housingAppointment) {
        this.HousingAppointment = housingAppointment;
    }

    public HousingAppointment getHousingAppointment() {
        return HousingAppointment;
    }
}
