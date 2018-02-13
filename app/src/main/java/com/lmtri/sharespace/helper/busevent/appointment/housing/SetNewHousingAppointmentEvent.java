package com.lmtri.sharespace.helper.busevent.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 11/28/2017.
 */

public class SetNewHousingAppointmentEvent {
    private HousingAppointment HousingAppointment;

    public SetNewHousingAppointmentEvent(HousingAppointment housingAppointment) {
        this.HousingAppointment = housingAppointment;
    }

    public HousingAppointment getHousingAppointment() {
        return HousingAppointment;
    }
}
