package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 12/2/2017.
 */

public class SavedHousing {
    private Housing SavedHousing;
    private Date DateTimeCreated;

    public Housing getSavedHousing() {
        return SavedHousing;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }
}
