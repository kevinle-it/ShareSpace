package com.lmtri.sharespace.api.service.user.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IHousingAppointmentGettingCallback {
    void onGetComplete(HousingAppointment housingAppointment);
    void onGetFailure(Throwable t);
}
