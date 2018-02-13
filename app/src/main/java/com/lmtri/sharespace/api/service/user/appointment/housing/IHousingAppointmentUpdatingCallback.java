package com.lmtri.sharespace.api.service.user.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface IHousingAppointmentUpdatingCallback {
    void onUpdateComplete(HousingAppointment housingAppointment);
    void onUpdateFailure(Throwable t);
}
