package com.lmtri.sharespace.api.service.user.appointment.sharehousing;

import com.lmtri.sharespace.api.model.ShareHousingAppointment;

/**
 * Created by lmtri on 12/4/2017.
 */

public interface IShareHousingAppointmentAcceptingCallback {
    void onAcceptComplete(ShareHousingAppointment shareHousingAppointment);
    void onAcceptFailure(Throwable t);
}
