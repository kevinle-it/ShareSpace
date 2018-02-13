package com.lmtri.sharespace.helper.busevent.sharehousing.availability;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 12/13/2017.
 */

public class UnhideShareHousingEvent {
    private ShareHousing ShareHousing;

    public UnhideShareHousingEvent(ShareHousing shareHousing) {
        ShareHousing = shareHousing;
    }

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }
}
