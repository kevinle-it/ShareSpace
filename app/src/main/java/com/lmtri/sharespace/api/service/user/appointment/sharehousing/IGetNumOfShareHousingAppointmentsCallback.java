package com.lmtri.sharespace.api.service.user.appointment.sharehousing;

/**
 * Created by lmtri on 12/5/2017.
 */

public interface IGetNumOfShareHousingAppointmentsCallback {
    void onGetComplete(Integer numOfShareHousingAppointments);
    void onGetFailure(Throwable t);
}
