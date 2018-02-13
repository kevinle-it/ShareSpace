package com.lmtri.sharespace.api.service.user.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 12/4/2017.
 */

public interface IHousingAppointmentAcceptingCallback {
    void onAcceptComplete(HousingAppointment housingAppointment);
    void onAcceptFailure(Throwable t);
}
