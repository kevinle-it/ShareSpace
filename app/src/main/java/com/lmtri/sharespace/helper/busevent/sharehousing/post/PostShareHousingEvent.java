package com.lmtri.sharespace.helper.busevent.sharehousing.post;

import com.lmtri.sharespace.api.model.ShareHousing;

/**
 * Created by lmtri on 11/29/2017.
 */

public class PostShareHousingEvent {
    private ShareHousing ShareHousing;

    public PostShareHousingEvent(ShareHousing shareHousing) {
        ShareHousing = shareHousing;
    }

    public ShareHousing getShareHousing() {
        return ShareHousing;
    }
}
