package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 8/21/2017.
 */

public class HousingAppointment {
    private Housing Housing;
    private User Sender;    // User.
    private int RecipientID;    // Owner.
    private Date AppointmentDateTime;
    private Date DateTimeCreated;
    private String Content;
    private boolean IsOwnerConfirmed;
    private boolean IsUserConfirmed;
    private int NumOfRequests;

    public Housing getHousing() {
        return Housing;
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

    public boolean isOwnerConfirmed() {
        return IsOwnerConfirmed;
    }

    public boolean isUserConfirmed() {
        return IsUserConfirmed;
    }

    public int getNumOfRequests() {
        return NumOfRequests;
    }
}
