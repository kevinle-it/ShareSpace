package com.lmtri.sharespace.api.service.user.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

import java.util.List;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IGetMoreNewerShareHousingAppointmentsCallback {
    void onGetComplete(List<ShareHousingAppointment> shareHousingAppointments);
    void onGetFailure(Throwable t);
}
