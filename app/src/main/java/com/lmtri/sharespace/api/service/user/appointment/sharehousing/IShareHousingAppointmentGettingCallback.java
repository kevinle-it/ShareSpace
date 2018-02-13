package com.lmtri.sharespace.api.service.user.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

/**
 * Created by lmtri on 12/2/2017.
 */

public interface IShareHousingAppointmentGettingCallback {
    void onGetComplete(ShareHousingAppointment shareHousingAppointment);
    void onGetFailure(Throwable t);
}
