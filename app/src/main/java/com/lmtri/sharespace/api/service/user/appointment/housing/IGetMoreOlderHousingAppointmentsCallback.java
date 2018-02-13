package com.lmtri.sharespace.api.service.user.appointment.housing;

import com.lmtri.sharespace.api.model.HousingAppointment;

import java.util.List;

/**
 * Created by lmtri on 8/21/2017.
 */

public interface IGetMoreOlderHousingAppointmentsCallback {
    void onGetComplete(List<HousingAppointment> housingAppointments);
    void onGetFailure(Throwable t);
}
