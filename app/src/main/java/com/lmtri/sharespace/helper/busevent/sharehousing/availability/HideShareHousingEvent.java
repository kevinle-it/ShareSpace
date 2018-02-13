package com.lmtri.sharespace.helper.busevent.sharehousing.availability;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 12/13/2017.
 */

public class HideShareHousingEvent {
    private ShareHousing ShareHousing;

    public HideShareHousingEvent(ShareHousing shareHousing) {
        ShareHousing = shareHousing;
    }

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }
}
