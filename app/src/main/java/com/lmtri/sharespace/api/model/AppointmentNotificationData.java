package com.lmtri.sharespace.api.model;

/**
 * Created by lmtri on 12/26/2017.
 */

public class AppointmentNotificationData {
    private String HousingOrShareHousingType;
    private int AppointmentType;    // Set New = 1, Edit = 2, Accept = 3, Delete = 4.
    private int HousingOrShareHousingID;
    private int SenderID;
    private int OwnerOrCreatorID;

    public AppointmentNotificationData(String housingOrShareHousingType,
                                       int appointmentType,
                                       int housingOrShareHousingID,
                                       int senderID,
                                       int ownerOrCreatorID) {
        HousingOrShareHousingType = housingOrShareHousingType;
        AppointmentType = appointmentType;
        HousingOrShareHousingID = housingOrShareHousingID;
        SenderID = senderID;
        OwnerOrCreatorID = ownerOrCreatorID;
    }

    public String getHousingOrShareHousingType() {
        return HousingOrShareHousingType;
    }

    public int getAppointmentType() {
        return AppointmentType;
    }

    public int getHousingOrShareHousingID() {
        return HousingOrShareHousingID;
    }

    public int getSenderID() {
        return SenderID;
    }

    public int getOwnerOrCreatorID() {
        return OwnerOrCreatorID;
    }
}
