package com.lmtri.sharespace.helper.busevent.sharehousing.post;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 12/3/2017.
 */

public class DeleteShareHousingEvent {
    private ShareHousing ShareHousing;

    public DeleteShareHousingEvent(ShareHousing shareHousing) {
        this.ShareHousing = shareHousing;
    }

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }
}
