package com.lmtri.sharespace.helper.busevent.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public class DeleteHousingAppointmentEvent {
    private HousingAppointment HousingAppointment;

    public DeleteHousingAppointmentEvent(HousingAppointment housingAppointment) {
        HousingAppointment = housingAppointment;
    }

    public HousingAppointment getHousingAppointment() {
        return HousingAppointment;
    }
}
