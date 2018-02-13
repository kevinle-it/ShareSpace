package com.lmtri.sharespace.api.service.user.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

/**
 * Created by lmtri on 12/1/2017.
 */

public interface IShareHousingAppointmentDeletingCallback {
    void onDeleteComplete(ShareHousingAppointment shareHousingAppointment);
    void onDeleteFailure(Throwable t);
}
