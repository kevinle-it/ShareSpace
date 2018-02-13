package com.lmtri.sharespace.api.model;

import java.util.Date;

/**
 * Created by lmtri on 12/10/2017.
 */

public class HistoryShareHousingNote {
    private ShareHousing ShareHousing;
    private Date DateTimeCreated;

    public HistoryShareHousingNote(ShareHousing shareHousing, Date dateTimeCreated) {
        ShareHousing = shareHousing;
        DateTimeCreated = dateTimeCreated;
    }

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }

    public Date getDateTimeCreated() {
        return DateTimeCreated;
    }
}
