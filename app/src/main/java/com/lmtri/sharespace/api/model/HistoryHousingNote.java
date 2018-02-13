package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 12/10/2017.
 */

public class HistoryHousingNote {
    private Housing Housing;
    private Date DateTimeCreated;

    public HistoryHousingNote(Housing housing, Date dateTimeCreated) {
        Housing = housing;
        DateTimeCreated = dateTimeCreated;
    }

    public Housing getHousing() {
        return Housing;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }
}
