package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 12/1/2017.
 */

public class ShareHousingAppointment {
    private ShareHousing ShareHousing;
    private User Sender;    // User
    private int RecipientID;    // Owner.
    private Date AppointmentDateTime;
    private Date DateTimeCreated;
    private String Content;
    private boolean IsCreatorConfirmed;
    private boolean IsUserConfirmed;
    private int NumOfRequests;

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }

    public User getSender() {
        return Sender;
    }

    public int getRecipientID() {
        return RecipientID;
    }

    public Date getAppointmentDateTime() {
        return AppointmentDateTime;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }

    public String getContent() {
        return Content;
    }

    public boolean isCreatorConfirmed() {
        return IsCreatorConfirmed;
    }

    public boolean isUserConfirmed() {
        return IsUserConfirmed;
    }

    public int getNumOfRequests() {
        return NumOfRequests;
    }
}
