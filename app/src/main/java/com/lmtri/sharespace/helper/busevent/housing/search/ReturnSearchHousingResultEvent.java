package com.lmtri.sharespace.helper.busevent.housing.search;

import com.lmtri.sharespace.api.model.Housing;

import java.util.List;

/**
 * Created by lmtri on 12/20/2017.
 */

public class ReturnSearchHousingResultEvent {
    private List<Housing> housingResults;

    public ReturnSearchHousingResultEvent(List<Housing> housingResults) {
        this.housingResults = housingResults;
    }

    public List<Housing> getHousingResults() {
        return housingResults;
    }
}
