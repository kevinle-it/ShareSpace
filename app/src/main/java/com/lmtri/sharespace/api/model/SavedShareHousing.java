package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 12/2/2017.
 */

public class SavedShareHousing {
    private ShareHousing SavedShareHousing;
    private Date DateTimeCreated;

    public ShareHousing getSavedShareHousing() {
        return SavedShareHousing;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }
}
