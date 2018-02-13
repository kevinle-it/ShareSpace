package com.lmtri.sharespace.helper.busevent.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 12/1/2017.
 */

public class UpdateHousingAppointmentEvent {
    private HousingAppointment HousingAppointment;

    public UpdateHousingAppointmentEvent(HousingAppointment housingAppointment) {
        HousingAppointment = housingAppointment;
    }

    public HousingAppointment getHousingAppointment() {
        return HousingAppointment;
    }
}
