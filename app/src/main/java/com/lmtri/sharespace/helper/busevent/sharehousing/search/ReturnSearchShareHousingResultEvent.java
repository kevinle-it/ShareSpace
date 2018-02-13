package com.lmtri.sharespace.helper.busevent.sharehousing.search;

import com.lmtri.sharespace.api.model.ShareHousing;

import java.util.List;

/**
 * Created by lmtri on 12/20/2017.
 */

public class ReturnSearchShareHousingResultEvent {
    private List<ShareHousing> shareHousingResults;

    public ReturnSearchShareHousingResultEvent(List<ShareHousing> shareHousingResults) {
        this.shareHousingResults = shareHousingResults;
    }

    public List<ShareHousing> getShareHousingResults() {
        return shareHousingResults;
    }
}
