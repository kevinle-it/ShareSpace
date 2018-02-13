package com.lmtri.sharespace.helper.busevent.sharehousing.post;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 11/30/2017.
 */

public class UpdateShareHousingEvent {
    private ShareHousing ShareHousing;

    public UpdateShareHousingEvent(ShareHousing shareHousing) {
        this.ShareHousing = shareHousing;
    }

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }
}
