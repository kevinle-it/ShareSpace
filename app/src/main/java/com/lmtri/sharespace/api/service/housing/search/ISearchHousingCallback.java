package com.lmtri.sharespace.api.service.housing.search;

import com.lmtri.sharespace.api.model.Housing;

import java.util.List;

/**
 * Created by lmtri on 12/20/2017.
 */

public interface ISearchHousingCallback {
    void onSearchComplete(List<Housing> housingResults);
    void onSearchFailure(Throwable t);
}
