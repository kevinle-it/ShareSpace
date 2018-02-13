package com.lmtri.sharespace.api.service.user.appointment.housing;

/**
 * Created by lmtri on 12/5/2017.
 */

public interface IGetNumOfHousingAppointmentsCallback {
    void onGetComplete(Integer numOfHousingAppointments);
    void onGetFailure(Throwable t);
}
